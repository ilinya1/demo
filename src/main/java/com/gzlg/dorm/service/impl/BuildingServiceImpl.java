package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.BuildingReq;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.entity.DormRoom;
import com.gzlg.dorm.mapper.DormBuildingMapper;
import com.gzlg.dorm.mapper.DormRoomMapper;
import com.gzlg.dorm.service.BuildingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 楼栋服务实现：楼栋名唯一；删除时该楼栋下有房间则拦截。
 */
@Service
public class BuildingServiceImpl implements BuildingService {

    private final DormBuildingMapper buildingMapper;
    private final DormRoomMapper roomMapper;

    public BuildingServiceImpl(DormBuildingMapper buildingMapper, DormRoomMapper roomMapper) {
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
    }

    @Override
    public List<Map<String, Object>> options() {
        return buildingMapper.selectList(null).stream()
                .map(b -> Map.<String, Object>of("id", b.getId(), "buildingName", b.getBuildingName()))
                .toList();
    }

    @Override
    public PageResult<DormBuilding> page(String buildingName, String manager, int page, int pageSize) {
        Page<DormBuilding> p = new Page<>(page, pageSize);
        buildingMapper.selectPage(p, Wrappers.<DormBuilding>lambdaQuery()
                .like(buildingName != null && !buildingName.isBlank(), DormBuilding::getBuildingName, buildingName)
                .like(manager != null && !manager.isBlank(), DormBuilding::getManager, manager)
                .orderByAsc(DormBuilding::getId));
        return PageResult.of(p.getRecords(), p.getTotal());
    }

    @Override
    public void create(BuildingReq req) {
        if (buildingMapper.selectCount(Wrappers.<DormBuilding>lambdaQuery()
                .eq(DormBuilding::getBuildingName, req.getBuildingName())) > 0) {
            throw new BizException("楼栋名已存在");
        }
        DormBuilding b = new DormBuilding();
        apply(b, req);
        buildingMapper.insert(b);
    }

    @Override
    public void update(Long id, BuildingReq req) {
        DormBuilding exist = buildingMapper.selectById(id);
        if (exist == null) {
            throw new BizException("楼栋不存在");
        }
        if (!req.getBuildingName().equals(exist.getBuildingName())
                && buildingMapper.selectCount(Wrappers.<DormBuilding>lambdaQuery()
                        .eq(DormBuilding::getBuildingName, req.getBuildingName())
                        .ne(DormBuilding::getId, id)) > 0) {
            throw new BizException("楼栋名已存在");
        }
        apply(exist, req);
        buildingMapper.updateById(exist);
    }

    @Override
    public void delete(Long id) {
        if (buildingMapper.selectById(id) == null) {
            throw new BizException("楼栋不存在");
        }
        if (roomMapper.selectCount(Wrappers.<DormRoom>lambdaQuery().eq(DormRoom::getBuildingId, id)) > 0) {
            throw new BizException("该楼栋下仍有房间，无法删除");
        }
        buildingMapper.deleteById(id);
    }

    private void apply(DormBuilding b, BuildingReq req) {
        b.setBuildingName(req.getBuildingName());
        b.setFloorCount(req.getFloorCount());
        b.setRoomCount(req.getRoomCount());
        b.setManager(req.getManager());
    }
}