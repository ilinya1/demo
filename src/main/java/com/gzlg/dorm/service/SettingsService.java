package com.gzlg.dorm.service;

import java.util.List;
import java.util.Map;

/**
 * 系统设置：系统参数 + 退宿原因字典。
 */
public interface SettingsService {

    List<Map<String, Object>> getParams();

    void updateParams(List<Map<String, Object>> list);

    void resetParams();

    List<Map<String, Object>> getCheckoutReasons();

    void createCheckoutReason(String name, Integer sort);

    void updateCheckoutReason(Long id, String name, Integer sort);

    void deleteCheckoutReason(Long id);
}