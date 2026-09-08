package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.HygieneRequest;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.entity.DormRoom;
import com.gzlg.dorm.entity.HygieneRecord;
import com.gzlg.dorm.mapper.DormBuildingMapper;
import com.gzlg.dorm.mapper.DormRoomMapper;
import com.gzlg.dorm.mapper.HygieneRecordMapper;
import com.gzlg.dorm.service.HygieneService;
import com.gzlg.dorm.vo.HygieneVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卫生检查实现。
 */
@Service
public class HygieneServiceImpl implements HygieneService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HygieneRecordMapper hygieneMapper;
    private final DormBuildingMapper buildingMapper;
    private final DormRoomMapper roomMapper;

    public HygieneServiceImpl(HygieneRecordMapper hygieneMapper, DormBuildingMapper buildingMapper,
                              DormRoomMapper roomMapper) {
        this.hygieneMapper = hygieneMapper;
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    public PageResult<HygieneVO> page(String checkDate, Long buildingId, Long roomId, String result,
                                      int page, int pageSize) {
        Page<HygieneRecord> p = new Page<>(page, pageSize);
        hygieneMapper.selectPage(p, Wrappers.<HygieneRecord>lambdaQuery()
                .eq(checkDate != null && !checkDate.isBlank(), HygieneRecord::getCheckDate, parse(checkDate))
                .eq(buildingId != null, HygieneRecord::getBuildingId, buildingId)
                .eq(roomId != null, HygieneRecord::getRoomId, roomId)
                .eq(result != null && !result.isBlank(), HygieneRecord::getResult, result)
                .orderByDesc(HygieneRecord::getCheckDate, HygieneRecord::getId));
        if (p.getRecords().isEmpty()) {
            return PageResult.of(List.of(), p.getTotal());
        }

        Map<Long, DormBuilding> buildings = new HashMap<>();
        for (Long bid : p.getRecords().stream().map(HygieneRecord::getBuildingId).distinct().toList()) {
            buildings.put(bid, buildingMapper.selectById(bid));
        }
        Map<Long, DormRoom> rooms = new HashMap<>();
        for (Long rid : p.getRecords().stream().map(HygieneRecord::getRoomId).distinct().toList()) {
            rooms.put(rid, roomMapper.selectById(rid));
        }

        return PageResult.of(p.getRecords().stream()
                .map(r -> toVO(r, buildings.get(r.getBuildingId()), rooms.get(r.getRoomId())))
                .toList(), p.getTotal());
    }

    @Override
    @Transactional
    public void create(HygieneRequest req) {
        int score = req.getScore() == null ? 0 : req.getScore();
        String result = score >= 90 ? "优秀" : (score >= 60 ? "合格" : "不合格");

        List<String> deductItems = req.getDeductItems() == null ? List.of() : req.getDeductItems();
        List<String> photos = req.getPhotos() == null ? List.of() : req.getPhotos();
        boolean violateElectric = deductItems.contains("违规电器");
        if ((score < 60 || violateElectric) && photos.isEmpty()) {
            throw new BizException("评分低于 60 或涉及违规电器时，必须上传现场照片");
        }

        HygieneRecord record = new HygieneRecord();
        record.setCheckDate(parse((req.getCheckDate())));
        record.setChecker(req.getChecker());
        record.setBuildingId(req.getBuildingId());
        record.setRoomId(req.getRoomId());
        record.setScore(score);
        record.setResult(result);
        record.setDeductItems(toJson(deductItems));
        record.setPhotos(toJson(photos));
        record.setComment(req.getComment());
        record.setCreatedAt(LocalDateTime.now());
        hygieneMapper.insert(record);
    }

    private HygieneVO toVO(HygieneRecord r, DormBuilding building, DormRoom room) {
        HygieneVO vo = new HygieneVO();
        vo.setId(r.getId());
        vo.setCheckDate(r.getCheckDate());
        vo.setChecker(r.getChecker());
        vo.setBuildingId(r.getBuildingId());
        vo.setRoomId(r.getRoomId());
        if (building != null) {
            vo.setBuildingName(building.getBuildingName());
        }
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
        }
        vo.setScore(r.getScore());
        vo.setResult(r.getResult());
        vo.setDeductItems(parseList(r.getDeductItems()));
        vo.setPhotos(parseList(r.getPhotos()));
        vo.setComment(r.getComment());
        return vo;
    }

    private static LocalDate parse(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new BizException("检查日期格式不正确");
        }
    }

    private static String toJson(List<String> list) {
        try {
            return new ObjectMapper().writeValueAsString(list == null ? List.of() : list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private static List<String> parseList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return new ObjectMapper().readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }
}