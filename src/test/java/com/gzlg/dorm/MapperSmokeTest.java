package com.gzlg.dorm;

import com.gzlg.dorm.mapper.ClazzMapper;
import com.gzlg.dorm.mapper.RepairTypeMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Mapper 冒烟测试：验证实体 ↔ 表/列映射正确（依赖本机 MySQL dorm_manager 与种子数据）。
 */
@SpringBootTest
class MapperSmokeTest {

    @Autowired
    private ClazzMapper clazzMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private RepairTypeMapper repairTypeMapper;

    @Test
    void seededTablesAreQueryable() {
        // class / repair_type 有种子数据，student 表含紧急联系人等新列
        assertThat(clazzMapper.selectCount(null)).isGreaterThan(0);
        assertThat(repairTypeMapper.selectCount(null)).isGreaterThan(0);
        // 触发含 emergency_phone 等字段的实体查询，验证列映射不报错
        assertThat(studentMapper.selectList(null)).isNotEmpty();
    }
}