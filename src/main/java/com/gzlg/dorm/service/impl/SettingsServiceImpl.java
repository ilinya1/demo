package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.entity.CheckoutApply;
import com.gzlg.dorm.entity.SysParameter;
import com.gzlg.dorm.mapper.CheckoutApplyMapper;
import com.gzlg.dorm.mapper.SysParameterMapper;
import com.gzlg.dorm.service.SettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 系统设置实现。系统参数持久化到 sys_parameter；退宿原因字典为内存静态列表（与前端 mock 一致，重启还原）。
 */
@Service
public class SettingsServiceImpl implements SettingsService {

    /** 默认系统参数（resetParams 写回的内置默认值，键对齐 sys_parameter.param_key） */
    private static final Map<String, String> DEFAULT_PARAMS = new LinkedHashMap<>();
    static {
        DEFAULT_PARAMS.put("systemName", "学生宿舍管理系统");
        DEFAULT_PARAMS.put("welcomeMessage", "欢迎使用学生宿舍管理系统");
        DEFAULT_PARAMS.put("contactPhone", "0571-88888888");
        DEFAULT_PARAMS.put("contactEmail", "dorm@example.edu.cn");
    }
    private static final Map<String, String> DEFAULT_PARAM_NAMES = new LinkedHashMap<>();
    static {
        DEFAULT_PARAM_NAMES.put("systemName", "系统名称");
        DEFAULT_PARAM_NAMES.put("welcomeMessage", "登录欢迎语");
        DEFAULT_PARAM_NAMES.put("contactPhone", "联系电话");
        DEFAULT_PARAM_NAMES.put("contactEmail", "联系邮箱");
    }

    /** 退宿原因内存字典（对齐 mock 初始：id1..5 sort1..5，重启还原） */
    private static final List<Map<String, Object>> CHECKOUT_REASONS = new ArrayList<>();
    private static final AtomicLong REASON_SEQ = new AtomicLong(5);
    static {
        CHECKOUT_REASONS.add(reason(1L, "毕业离校", 1));
        CHECKOUT_REASONS.add(reason(2L, "休学", 2));
        CHECKOUT_REASONS.add(reason(3L, "退学", 3));
        CHECKOUT_REASONS.add(reason(4L, "调宿", 4));
        CHECKOUT_REASONS.add(reason(5L, "其他", 5));
    }

    private final SysParameterMapper sysParameterMapper;
    private final CheckoutApplyMapper checkoutApplyMapper;

    public SettingsServiceImpl(SysParameterMapper sysParameterMapper, CheckoutApplyMapper checkoutApplyMapper) {
        this.sysParameterMapper = sysParameterMapper;
        this.checkoutApplyMapper = checkoutApplyMapper;
    }

    private static Map<String, Object> reason(long id, String name, int sort) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("sort", sort);
        return m;
    }

    @Override
    public List<Map<String, Object>> getParams() {
        List<SysParameter> list = sysParameterMapper.selectList(
                Wrappers.<SysParameter>lambdaQuery().orderByAsc(SysParameter::getId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysParameter p : list) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("key", p.getParamKey());
            m.put("param", p.getParamName());
            m.put("name", p.getParamName());
            m.put("value", p.getParamValue());
            result.add(m);
        }
        return result;
    }

    @Override
    @Transactional
    public void updateParams(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            throw new BizException("参数不能为空");
        }
        for (Map<String, Object> item : list) {
            String key = asString(item.get("key"));
            if (key == null || key.isBlank()) {
                continue;
            }
            SysParameter param = sysParameterMapper.selectOne(
                    Wrappers.<SysParameter>lambdaQuery().eq(SysParameter::getParamKey, key));
            if (param == null) {
                continue;
            }
            param.setParamValue(asString(item.getOrDefault("value", "")));
            param.setUpdatedAt(java.time.LocalDateTime.now());
            sysParameterMapper.updateById(param);
        }
    }

    @Override
    @Transactional
    public void resetParams() {
        for (Map.Entry<String, String> entry : DEFAULT_PARAMS.entrySet()) {
            SysParameter param = sysParameterMapper.selectOne(
                    Wrappers.<SysParameter>lambdaQuery().eq(SysParameter::getParamKey, entry.getKey()));
            if (param == null) {
                continue;
            }
            param.setParamName(DEFAULT_PARAM_NAMES.get(entry.getKey()));
            param.setParamValue(entry.getValue());
            param.setUpdatedAt(java.time.LocalDateTime.now());
            sysParameterMapper.updateById(param);
        }
    }

    @Override
    public List<Map<String, Object>> getCheckoutReasons() {
        List<Map<String, Object>> result = new ArrayList<>(CHECKOUT_REASONS);
        result.sort((a, b) -> Integer.compare((Integer) a.get("sort"), (Integer) b.get("sort")));
        return result;
    }

    @Override
    public void createCheckoutReason(String name, Integer sort) {
        String n = trimReason(name);
        boolean exists = CHECKOUT_REASONS.stream().anyMatch(r -> n.equals(r.get("name")));
        if (exists) {
            throw new BizException("该退宿原因已存在");
        }
        int s = sort == null ? 99 : sort;
        CHECKOUT_REASONS.add(reason(REASON_SEQ.incrementAndGet(), n, s));
    }

    @Override
    public void updateCheckoutReason(Long id, String name, Integer sort) {
        Map<String, Object> target = CHECKOUT_REASONS.stream()
                .filter(r -> Long.valueOf(r.get("id").toString()).equals(id))
                .findFirst()
                .orElseThrow(() -> new BizException("退宿原因不存在"));
        String n = trimReason(name);
        boolean dup = CHECKOUT_REASONS.stream()
                .anyMatch(r -> !Long.valueOf(r.get("id").toString()).equals(id) && n.equals(r.get("name")));
        if (dup) {
            throw new BizException("该退宿原因已存在");
        }
        target.put("name", n);
        if (sort != null) {
            target.put("sort", sort);
        }
    }

    @Override
    public void deleteCheckoutReason(Long id) {
        Map<String, Object> target = CHECKOUT_REASONS.stream()
                .filter(r -> Long.valueOf(r.get("id").toString()).equals(id))
                .findFirst()
                .orElseThrow(() -> new BizException("退宿原因不存在"));
        Long cnt = checkoutApplyMapper.selectCount(
                Wrappers.<CheckoutApply>lambdaQuery().eq(CheckoutApply::getReason, target.get("name")));
        if (cnt != null && cnt > 0) {
            throw new BizException("该原因已被退宿申请引用，无法删除");
        }
        CHECKOUT_REASONS.removeIf(r -> Long.valueOf(r.get("id").toString()).equals(id));
    }

    private String trimReason(String name) {
        if (name == null) {
            throw new BizException("请输入原因名称");
        }
        String n = name.trim();
        if (n.isEmpty()) {
            throw new BizException("请输入原因名称");
        }
        return n;
    }

    private String asString(Object o) {
        return o == null ? null : o.toString();
    }
}