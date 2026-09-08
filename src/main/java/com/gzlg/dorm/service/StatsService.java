package com.gzlg.dorm.service;

import java.util.Map;

/**
 * 统计与仪表盘聚合服务。全部聚合从真实数据库表计算。
 */
public interface StatsService {

    Map<String, Object> occupancy();

    Map<String, Object> hygiene();

    Map<String, Object> repair();

    Map<String, Object> dashboardStats();

    Map<String, Object> buildingOccupancy();

    Map<String, Object> hygieneTrend();

    Map<String, Object> workbench();
}