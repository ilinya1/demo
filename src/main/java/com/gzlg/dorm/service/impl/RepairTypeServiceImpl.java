package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.dto.RepairTypeRequest;
import com.gzlg.dorm.entity.RepairOrder;
import com.gzlg.dorm.entity.RepairType;
import com.gzlg.dorm.mapper.RepairOrderMapper;
import com.gzlg.dorm.mapper.RepairTypeMapper;
import com.gzlg.dorm.service.RepairTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报修类型字典实现。
 */
@Service
public class RepairTypeServiceImpl implements RepairTypeService {

    private final RepairTypeMapper repairTypeMapper;
    private final RepairOrderMapper repairOrderMapper;

    public RepairTypeServiceImpl(RepairTypeMapper repairTypeMapper, RepairOrderMapper repairOrderMapper) {
        this.repairTypeMapper = repairTypeMapper;
        this.repairOrderMapper = repairOrderMapper;
    }

    @Override
    public List<RepairType> list() {
        return repairTypeMapper.selectList(Wrappers.<RepairType>lambdaQuery()
                .orderByAsc(RepairType::getSort));
    }

    @Override
    @Transactional
    public void create(RepairTypeRequest req) {
        String name = trim(req.getName());
        checkNameConflict(name, null);
        RepairType type = new RepairType();
        type.setName(name);
        type.setSort(req.getSort());
        long maxId = 0;
        RepairType max = repairTypeMapper.selectOne(Wrappers.<RepairType>lambdaQuery()
                .orderByDesc(RepairType::getId).last("limit 1"));
        if (max != null) {
            maxId = max.getId();
        }
        type.setId(maxId + 1);
        type.setCreatedAt(LocalDateTime.now());
        repairTypeMapper.insert(type);
    }

    @Override
    @Transactional
    public void update(Long id, RepairTypeRequest req) {
        RepairType type = repairTypeMapper.selectById(id);
        if (type == null) {
            throw new BizException("该报修类型不存在");
        }
        String name = trim(req.getName());
        checkNameConflict(name, id);
        type.setName(name);
        type.setSort(req.getSort());
        repairTypeMapper.updateById(type);
    }

    @Override
    public void delete(Long id) {
        Long cnt = repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                .eq(RepairOrder::getTypeId, id));
        if (cnt != null && cnt > 0) {
            throw new BizException("该类型已被报修单引用，无法删除");
        }
        repairTypeMapper.deleteById(id);
    }

    private String trim(String name) {
        if (name == null) {
            throw new BizException("请输入类型名称");
        }
        String n = name.trim();
        if (n.isEmpty()) {
            throw new BizException("请输入类型名称");
        }
        return n;
    }

    private void checkNameConflict(String name, Long excludeId) {
        Long cnt = repairTypeMapper.selectCount(Wrappers.<RepairType>lambdaQuery()
                .eq(RepairType::getName, name)
                .ne(excludeId != null, RepairType::getId, excludeId));
        if (cnt != null && cnt > 0) {
            throw new BizException("该报修类型已存在");
        }
    }
}