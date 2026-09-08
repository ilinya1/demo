package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gzlg.dorm.entity.CheckIn;
import com.gzlg.dorm.entity.CheckoutApply;
import com.gzlg.dorm.entity.DormBed;
import com.gzlg.dorm.entity.DormBuilding;
import com.gzlg.dorm.entity.DormRoom;
import com.gzlg.dorm.entity.HygieneRecord;
import com.gzlg.dorm.entity.RepairOrder;
import com.gzlg.dorm.entity.RepairType;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.mapper.CheckInMapper;
import com.gzlg.dorm.mapper.CheckoutApplyMapper;
import com.gzlg.dorm.mapper.DormBedMapper;
import com.gzlg.dorm.mapper.DormBuildingMapper;
import com.gzlg.dorm.mapper.DormRoomMapper;
import com.gzlg.dorm.mapper.HygieneRecordMapper;
import com.gzlg.dorm.mapper.RepairOrderMapper;
import com.gzlg.dorm.mapper.RepairTypeMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.service.StatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计与仪表盘聚合实现。数据量小的统计在 Java 端基于 MyBatis-Plus Wrapper 汇总。
 */
@Service
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String OCCUPIED = "占用";
    private static final String IN_HOUSE = "在住";
    private static final String CHECKED_OUT = "已退宿";
    private static final String PENDING = "待审核";
    private static final String PASSED = "已通过";
    private static final String REJECTED = "已驳回";
    private static final String PENDING_REPAIR = "待处理";
    private static final String PROCESSING = "处理中";
    private static final String DONE_REPAIR = "已完成";
    private static final String EXCELLENT = "优秀";
    private static final String QUALIFIED = "合格";
    private static final String UNQUALIFIED = "不合格";

    private final StudentMapper studentMapper;
    private final DormBuildingMapper buildingMapper;
    private final DormRoomMapper roomMapper;
    private final DormBedMapper bedMapper;
    private final CheckInMapper checkInMapper;
    private final CheckoutApplyMapper checkoutApplyMapper;
    private final HygieneRecordMapper hygieneRecordMapper;
    private final RepairOrderMapper repairOrderMapper;
    private final RepairTypeMapper repairTypeMapper;

    public StatsServiceImpl(StudentMapper studentMapper, DormBuildingMapper buildingMapper,
                            DormRoomMapper roomMapper, DormBedMapper bedMapper,
                            CheckInMapper checkInMapper, CheckoutApplyMapper checkoutApplyMapper,
                            HygieneRecordMapper hygieneRecordMapper, RepairOrderMapper repairOrderMapper,
                            RepairTypeMapper repairTypeMapper) {
        this.studentMapper = studentMapper;
        this.buildingMapper = buildingMapper;
        this.roomMapper = roomMapper;
        this.bedMapper = bedMapper;
        this.checkInMapper = checkInMapper;
        this.checkoutApplyMapper = checkoutApplyMapper;
        this.hygieneRecordMapper = hygieneRecordMapper;
        this.repairOrderMapper = repairOrderMapper;
        this.repairTypeMapper = repairTypeMapper;
    }

    // ---------- 台账：入住率 ----------

    @Override
    public Map<String, Object> occupancy() {
        List<DormRoom> rooms = roomMapper.selectList(null);
        Map<Long, DormRoom> roomById = rooms.stream()
                .collect(Collectors.toMap(DormRoom::getId, r -> r, (a, b) -> a));

        List<DormBed> beds = bedMapper.selectList(null);
        int totalBeds = beds.size();
        int occupiedBeds = (int) beds.stream().filter(b -> OCCUPIED.equals(b.getStatus())).count();

        // 按楼栋聚合床位
        Map<Long, int[]> buildAgg = new LinkedHashMap<>(); // buildingId -> [total, occupied]
        for (DormRoom room : rooms) {
            buildAgg.computeIfAbsent(room.getBuildingId(), k -> new int[]{0, 0});
        }
        for (DormBed bed : beds) {
            DormRoom room = roomById.get(bed.getRoomId());
            if (room == null) {
                continue;
            }
            int[] agg = buildAgg.computeIfAbsent(room.getBuildingId(), k -> new int[]{0, 0});
            agg[0]++;
            if (OCCUPIED.equals(bed.getStatus())) {
                agg[1]++;
            }
        }

        List<Map<String, Object>> byBuilding = new ArrayList<>();
        for (DormBuilding b : buildingMapper.selectList(null)) {
            int[] agg = buildAgg.getOrDefault(b.getId(), new int[]{0, 0});
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("building", b.getBuildingName());
            item.put("total", agg[0]);
            item.put("occupied", agg[1]);
            item.put("rate", round4(rate(agg[1], agg[0])));
            byBuilding.add(item);
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        for (YearMonth ym : lastMonths(6)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", ym.getMonthValue() + "月");
            item.put("checkIn", checkInMapper.selectCount(Wrappers.<CheckIn>lambdaQuery()
                    .ge(CheckIn::getCheckInTime, start(ym))
                    .lt(CheckIn::getCheckInTime, nextStart(ym))).intValue());
            item.put("checkOut", checkInMapper.selectCount(Wrappers.<CheckIn>lambdaQuery()
                    .ge(CheckIn::getCheckOutTime, start(ym))
                    .lt(CheckIn::getCheckOutTime, nextStart(ym))
                    .isNotNull(CheckIn::getCheckOutTime)).intValue());
            trend.add(item);
        }

        Map<String, Object> cards = new LinkedHashMap<>();
        cards.put("totalBeds", totalBeds);
        cards.put("occupiedBeds", occupiedBeds);
        cards.put("rate", round4(rate(occupiedBeds, totalBeds)));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cards", cards);
        map.put("byBuilding", byBuilding);
        map.put("trend", trend);
        return map;
    }

    // ---------- 台账：卫生 ----------

    @Override
    public Map<String, Object> hygiene() {
        List<HygieneRecord> records = hygieneRecordMapper.selectList(null);

        int total = records.size();
        int excellent = (int) records.stream().filter(r -> EXCELLENT.equals(r.getResult())).count();
        int pass = (int) records.stream().filter(r -> QUALIFIED.equals(r.getResult())).count();
        int fail = (int) records.stream().filter(r -> UNQUALIFIED.equals(r.getResult())).count();
        int avg = total == 0 ? 0 : (int) Math.round(records.stream()
                .map(HygieneRecord::getScore)
                .filter(s -> s != null)
                .mapToInt(Integer::intValue)
                .average().orElse(0));

        Map<String, Object> cards = new LinkedHashMap<>();
        cards.put("total", total);
        cards.put("excellent", excellent);
        cards.put("pass", pass);
        cards.put("fail", fail);
        cards.put("avg", avg);

        List<Map<String, Object>> byWeek = new ArrayList<>();
        List<Bucket> weeks = lastWeeks(4);
        for (int i = 0; i < weeks.size(); i++) {
            Bucket wk = weeks.get(i);
            List<Integer> scores = records.stream()
                    .filter(r -> r.getCheckDate() != null
                            && !r.getCheckDate().isBefore(wk.start())
                            && r.getCheckDate().isBefore(wk.end()))
                    .map(HygieneRecord::getScore)
                    .filter(s -> s != null)
                    .toList();
            int avgScore = scores.isEmpty() ? 0 : (int) Math.round(scores.stream().mapToInt(Integer::intValue)
                    .average().orElse(0));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("week", "第" + (i + 1) + "周");
            item.put("avg", avgScore);
            byWeek.add(item);
        }

        List<Map<String, Object>> statusDist = new ArrayList<>();
        statusDist.add(statusItem(EXCELLENT, excellent));
        statusDist.add(statusItem(QUALIFIED, pass));
        statusDist.add(statusItem(UNQUALIFIED, fail));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cards", cards);
        map.put("byWeek", byWeek);
        map.put("statusDist", statusDist);
        return map;
    }

    @Override
    public Map<String, Object> repair() {
        List<RepairOrder> orders = repairOrderMapper.selectList(null);
        int total = orders.size();
        int done = (int) orders.stream().filter(o -> DONE_REPAIR.equals(o.getStatus())).count();

        Map<Long, String> typeNameById = repairTypeMapper.selectList(null).stream()
                .collect(Collectors.toMap(RepairType::getId, RepairType::getName, (a, b) -> a));
        Map<String, Integer> byTypeAgg = new LinkedHashMap<>();
        for (RepairOrder o : orders) {
            String name = typeNameById.get(o.getTypeId());
            String type = name == null ? "其他" : name;
            byTypeAgg.merge(type, 1, Integer::sum);
        }
        List<Map<String, Object>> byType = new ArrayList<>();
        byTypeAgg.forEach((type, count) -> byType.add(Map.of("type", type, "count", count)));

        List<Map<String, Object>> trend = new ArrayList<>();
        for (YearMonth ym : lastMonths(6)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", ym.getMonthValue() + "月");
            item.put("submit", repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                    .ge(RepairOrder::getCreateTime, start(ym))
                    .lt(RepairOrder::getCreateTime, nextStart(ym))).intValue());
            item.put("done", repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                    .ge(RepairOrder::getHandleTime, start(ym))
                    .lt(RepairOrder::getHandleTime, nextStart(ym))
                    .isNotNull(RepairOrder::getHandleTime)).intValue());
            trend.add(item);
        }

        Map<String, Object> cards = new LinkedHashMap<>();
        cards.put("total", total);
        cards.put("done", done);
        cards.put("rate", round4(rate(done, total)));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("cards", cards);
        map.put("byType", byType);
        map.put("trend", trend);
        return map;
    }

    // ---------- 仪表盘 ----------

    @Override
    public Map<String, Object> dashboardStats() {
        int totalBeds = bedMapper.selectCount(null).intValue();
        int occupiedBeds = bedMapper.selectCount(Wrappers.<DormBed>lambdaQuery()
                .eq(DormBed::getStatus, OCCUPIED)).intValue();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("studentCount", studentMapper.selectCount(null).intValue());
        map.put("buildingCount", buildingMapper.selectCount(null).intValue());
        map.put("roomCount", roomMapper.selectCount(null).intValue());
        map.put("occupancyRate", rate(occupiedBeds, totalBeds));
        return map;
    }

    @Override
    public Map<String, Object> buildingOccupancy() {
        List<DormRoom> rooms = roomMapper.selectList(null);
        Map<Long, DormRoom> roomById = rooms.stream()
                .collect(Collectors.toMap(DormRoom::getId, r -> r, (a, b) -> a));
        Map<Long, int[]> agg = new LinkedHashMap<>();
        for (DormRoom room : rooms) {
            agg.computeIfAbsent(room.getBuildingId(), k -> new int[]{0, 0});
        }
        for (DormBed bed : bedMapper.selectList(null)) {
            DormRoom room = roomById.get(bed.getRoomId());
            if (room == null) {
                continue;
            }
            int[] v = agg.computeIfAbsent(room.getBuildingId(), k -> new int[]{0, 0});
            v[0]++;
            if (OCCUPIED.equals(bed.getStatus())) {
                v[1]++;
            }
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (DormBuilding b : buildingMapper.selectList(null)) {
            int[] v = agg.getOrDefault(b.getId(), new int[]{0, 0});
            list.add(Map.of("building", b.getBuildingName(), "rate", rate(v[1], v[0])));
        }
        return Map.of("list", list);
    }

    @Override
    public Map<String, Object> hygieneTrend() {
        List<HygieneRecord> records = hygieneRecordMapper.selectList(null);
        List<Map<String, Object>> list = new ArrayList<>();
        List<Bucket> weeks = lastWeeks(4);
        for (int i = 0; i < weeks.size(); i++) {
            Bucket wk = weeks.get(i);
            List<Integer> scores = records.stream()
                    .filter(r -> r.getCheckDate() != null
                            && !r.getCheckDate().isBefore(wk.start())
                            && r.getCheckDate().isBefore(wk.end()))
                    .map(HygieneRecord::getScore)
                    .filter(s -> s != null)
                    .toList();
            int score = scores.isEmpty() ? 0 : (int) Math.round(scores.stream().mapToInt(Integer::intValue)
                    .average().orElse(0));
            list.add(Map.of("week", "第" + (i + 1) + "周", "score", score));
        }
        return Map.of("list", list);
    }

    @Override
    public Map<String, Object> workbench() {
        int pendingCheckout = checkoutApplyMapper.selectCount(Wrappers.<CheckoutApply>lambdaQuery()
                .eq(CheckoutApply::getStatus, PENDING)).intValue();
        int passedCheckout = checkoutApplyMapper.selectCount(Wrappers.<CheckoutApply>lambdaQuery()
                .eq(CheckoutApply::getStatus, PASSED)).intValue();
        int rejectedCheckout = checkoutApplyMapper.selectCount(Wrappers.<CheckoutApply>lambdaQuery()
                .eq(CheckoutApply::getStatus, REJECTED)).intValue();
        int pendingRepair = repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                .eq(RepairOrder::getStatus, PENDING_REPAIR)).intValue();
        int processingRepair = repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                .eq(RepairOrder::getStatus, PROCESSING)).intValue();
        int doneRepair = repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                .eq(RepairOrder::getStatus, DONE_REPAIR)).intValue();
        int badHygiene = hygieneRecordMapper.selectCount(Wrappers.<HygieneRecord>lambdaQuery()
                .eq(HygieneRecord::getResult, UNQUALIFIED)).intValue();
        int inHouse = checkInMapper.selectCount(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getStatus, IN_HOUSE)).intValue();
        int outHouse = checkInMapper.selectCount(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getStatus, CHECKED_OUT)).intValue();

        Map<String, Object> todos = new LinkedHashMap<>();
        todos.put("pendingCheckout", pendingCheckout);
        todos.put("pendingRepair", pendingRepair);
        todos.put("badHygiene", badHygiene);
        todos.put("inHouse", inHouse);
        todos.put("outHouse", outHouse);

        Map<String, Object> backlog = new LinkedHashMap<>();
        backlog.put("repair", List.of(
                backlogItem(PENDING_REPAIR, pendingRepair),
                backlogItem(PROCESSING, processingRepair),
                backlogItem(DONE_REPAIR, doneRepair)));
        backlog.put("checkout", List.of(
                backlogItem(PENDING, pendingCheckout),
                backlogItem(PASSED, passedCheckout),
                backlogItem(REJECTED, rejectedCheckout)));

        List<Map<String, Object>> alerts = new ArrayList<>();
        int overdueRepair = repairOrderMapper.selectCount(Wrappers.<RepairOrder>lambdaQuery()
                .eq(RepairOrder::getStatus, PENDING_REPAIR)
                .lt(RepairOrder::getCreateTime, LocalDateTime.now().minusDays(3))).intValue();
        if (overdueRepair > 0) {
            alerts.add(alert("danger", "repair", overdueRepair + " 条报修超 3 天未处理"));
        } else if (pendingRepair > 0) {
            alerts.add(alert("warning", "repair", pendingRepair + " 条报修待处理，请及时派单"));
        }
        if (badHygiene > 0) {
            alerts.add(alert("danger", "hygiene", "本周期存在 " + badHygiene + " 间卫生不合格宿舍，需复查整改"));
        }
        if (pendingCheckout > 0) {
            alerts.add(alert("warning", "checkout", pendingCheckout + " 条退宿申请待审核"));
        }
        if (alerts.isEmpty()) {
            alerts.add(alert("success", "all", "各项运行良好，暂无待办告警"));
        }

        Map<String, Object> feeds = new LinkedHashMap<>();
        feeds.put("repair", latestRepairFeeds());
        feeds.put("checkout", latestCheckoutFeeds());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("todos", todos);
        map.put("backlog", backlog);
        map.put("alerts", alerts);
        map.put("feeds", feeds);
        return map;
    }

    // ---------- feeds ----------

    private List<Map<String, Object>> latestRepairFeeds() {
        List<RepairOrder> orders = repairOrderMapper.selectList(Wrappers.<RepairOrder>lambdaQuery()
                .orderByDesc(RepairOrder::getCreateTime));
        if (orders.size() > 5) {
            orders = orders.subList(0, 5);
        }
        Map<Long, String> roomNoById = roomMapper.selectList(null).stream()
                .collect(Collectors.toMap(DormRoom::getId, DormRoom::getRoomNo, (a, b) -> a));
        Map<Long, String> typeNameById = repairTypeMapper.selectList(null).stream()
                .collect(Collectors.toMap(RepairType::getId, RepairType::getName, (a, b) -> a));
        List<Map<String, Object>> list = new ArrayList<>();
        for (RepairOrder o : orders) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", o.getId());
            item.put("type", "repair");
            String roomNo = roomNoById.get(o.getRoomId());
            String typeName = typeNameById.get(o.getTypeId());
            item.put("title", (roomNo == null ? "" : roomNo) + "室 · " + (typeName == null ? "" : typeName));
            item.put("desc", o.getDescription());
            item.put("status", o.getStatus());
            item.put("time", o.getCreateTime() == null ? null : o.getCreateTime().format(DT_FMT));
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> latestCheckoutFeeds() {
        List<CheckoutApply> applies = checkoutApplyMapper.selectList(Wrappers.<CheckoutApply>lambdaQuery()
                .orderByDesc(CheckoutApply::getCreateTime));
        if (applies.size() > 5) {
            applies = applies.subList(0, 5);
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (CheckoutApply a : applies) {
            Student student = studentMapper.selectById(a.getStudentId());
            String studentName = student == null ? "" : student.getName();
            String roomNo = latestCheckInRoomNo(a.getStudentId());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("type", "checkout");
            item.put("title", studentName + " · " + (roomNo == null ? "" : roomNo) + "室");
            String plan = a.getPlanDate() == null ? "" : a.getPlanDate().format(DATE_FMT);
            item.put("desc", a.getReason() + "（计划 " + plan + "）");
            item.put("status", a.getStatus());
            item.put("time", a.getCreateTime() == null ? null : a.getCreateTime().format(DT_FMT));
            list.add(item);
        }
        return list;
    }

    private String latestCheckInRoomNo(String studentId) {
        CheckIn ci = checkInMapper.selectOne(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getStudentId, studentId)
                .orderByDesc(CheckIn::getId)
                .last("limit 1"));
        return ci == null ? null : ci.getRoomNo();
    }

    // ---------- 工具 ----------

    private static Map<String, Object> statusItem(String name, int value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("value", value);
        return m;
    }

    private static Map<String, Object> backlogItem(String label, int value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("label", label);
        m.put("value", value);
        return m;
    }

    private static Map<String, Object> alert(String level, String type, String text) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("level", level);
        m.put("type", type);
        m.put("text", text);
        return m;
    }

    /** 最近 n 个自然月，从旧到新 */
    private static List<YearMonth> lastMonths(int n) {
        List<YearMonth> months = new ArrayList<>();
        YearMonth cur = YearMonth.from(LocalDate.now());
        for (int i = n - 1; i >= 0; i--) {
            months.add(cur.minusMonths(i));
        }
        return months;
    }

    /** 最近 n 周（每 7 天一桶），从旧到新；最新为第 n 周 */
    private static List<Bucket> lastWeeks(int n) {
        LocalDate today = LocalDate.now();
        List<Bucket> weeks = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            LocalDate endExclusive = today.minusDays(i * 7L).plusDays(1);
            LocalDate start = endExclusive.minusDays(7);
            weeks.add(new Bucket(start, endExclusive));
        }
        return weeks;
    }

    private static LocalDateTime start(YearMonth ym) {
        return ym.atDay(1).atStartOfDay();
    }

    private static LocalDateTime nextStart(YearMonth ym) {
        return ym.plusMonths(1).atDay(1).atStartOfDay();
    }

    private static double rate(int part, int total) {
        return total == 0 ? 0 : part / (double) total;
    }

    private static double round4(double v) {
        return Math.round(v * 10000) / 10000.0;
    }

    private record Bucket(LocalDate start, LocalDate end) {
    }
}