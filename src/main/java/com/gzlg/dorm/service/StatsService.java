package com.gzlg.dorm.service;

import java.util.List;
import java.util.Map;

/**
 * 统计与仪表盘聚合服务。全部聚合从真实数据库表计算。
 */
public interface StatsService {

    Map<String, Object> occupancy();

    Map<String, Object> hygiene();

    Map<String, Object> repair();

    Map<String, Object> dashboardStats();

    List<Map<String, Object>> buildingOccupancy();

    List<Map<String, Object>> hygieneTrend();

    Map<String, Object> workbench();
}