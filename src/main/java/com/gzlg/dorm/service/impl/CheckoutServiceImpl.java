package com.gzlg.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzlg.dorm.common.exception.BizException;
import com.gzlg.dorm.common.jwt.UserContext;
import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.util.BedNoUtil;
import com.gzlg.dorm.dto.AuditRequest;
import com.gzlg.dorm.dto.CheckoutApplyRequest;
import com.gzlg.dorm.dto.DirectCheckoutRequest;
import com.gzlg.dorm.entity.CheckIn;
import com.gzlg.dorm.entity.CheckoutApply;
import com.gzlg.dorm.entity.Student;
import com.gzlg.dorm.mapper.CheckInMapper;
import com.gzlg.dorm.mapper.CheckoutApplyMapper;
import com.gzlg.dorm.mapper.StudentMapper;
import com.gzlg.dorm.service.CheckInService;
import com.gzlg.dorm.service.CheckoutService;
import com.gzlg.dorm.vo.CheckoutAppVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 退宿业务实现：申请提交/审核（通过走 apply 退宿）/撤销/直接退宿（direct）。
 */
@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final String PENDING = "待审核";

    private final CheckoutApplyMapper applyMapper;
    private final StudentMapper studentMapper;
    private final CheckInMapper checkInMapper;
    private final CheckInService checkInService;

    public CheckoutServiceImpl(CheckoutApplyMapper applyMapper, StudentMapper studentMapper,
                               CheckInMapper checkInMapper, CheckInService checkInService) {
        this.applyMapper = applyMapper;
        this.studentMapper = studentMapper;
        this.checkInMapper = checkInMapper;
        this.checkInService = checkInService;
    }

    @Override
    public PageResult<CheckoutAppVO> listApps(String applyNo, String studentId, String status,
                                              int page, int pageSize) {
        Page<CheckoutApply> p = new Page<>(page, pageSize);
        applyMapper.selectPage(p, Wrappers.<CheckoutApply>lambdaQuery()
                .like(applyNo != null && !applyNo.isBlank(), CheckoutApply::getApplyNo, applyNo)
                .like(studentId != null && !studentId.isBlank(), CheckoutApply::getStudentId, studentId)
                .eq(status != null && !status.isBlank(), CheckoutApply::getStatus, status)
                .orderByAsc(CheckoutApply::getId));
        if (p.getRecords().isEmpty()) {
            return PageResult.of(List.of(), p.getTotal());
        }

        Map<String, Student> students = studentMapper.selectBatchIds(
                        p.getRecords().stream().map(CheckoutApply::getStudentId).distinct().toList())
                .stream().collect(Collectors.toMap(Student::getStudentId, Function.identity()));
        Map<String, CheckIn> latestRecord = p.getRecords().stream()
                .map(CheckoutApply::getStudentId).distinct()
                .collect(Collectors.toMap(sid -> sid, sid -> latestCheckIn(sid)));

        return PageResult.of(p.getRecords().stream().map(a -> toVO(a, students.get(a.getStudentId()),
                latestRecord.get(a.getStudentId()))).toList(), p.getTotal());
    }

    @Override
    @Transactional
    public void submitApply(CheckoutApplyRequest req) {
        Student student = studentMapper.selectById(req.getStudentId());
        if (student == null) {
            throw new BizException("学生不存在");
        }
        if (!"在住".equals(student.getHousingStatus())) {
            throw new BizException("当前不在住，无需办理退宿");
        }
        if (applyMapper.selectCount(Wrappers.<CheckoutApply>lambdaQuery()
                .eq(CheckoutApply::getStudentId, req.getStudentId())
                .eq(CheckoutApply::getStatus, PENDING)) > 0) {
            throw new BizException("您有未审核的退宿申请，请等待审核");
        }
        // 锁定同一学生并发提交
        // 单号：前缀+毫秒时间戳+4位随机，避免并发撞号（唯一键兜底）
        String applyNo = "TS" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
        CheckoutApply app = new CheckoutApply();
        app.setApplyNo(applyNo);
        app.setStudentId(req.getStudentId());
        app.setReason(req.getReason());
        app.setPlanDate(parseLocalDate(req.getPlanDate()));
        app.setDescription(req.getDescription());
        app.setStatus(PENDING);
        app.setCreateTime(LocalDateTime.now());
        applyMapper.insert(app);
    }

    @Override
    @Transactional
    public void audit(Long id, AuditRequest req) {
        CheckoutApply app = applyMapper.selectById(id);
        if (app == null) {
            throw new BizException("申请不存在");
        }
        if (!PENDING.equals(app.getStatus())) {
            throw new BizException("该申请已处理，请勿重复操作");
        }
        boolean approve = Boolean.TRUE.equals(req.getApprove());
        if (approve) {
            checkInService.checkout(app.getStudentId(), LocalDateTime.now(), "apply", "");
            app.setStatus("已通过");
        } else {
            app.setRejectReason(req.getRejectReason());
            app.setStatus("已驳回");
        }
        app.setAuditTime(LocalDateTime.now());
        applyMapper.updateById(app);
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        CheckoutApply app = applyMapper.selectById(id);
        if (app == null) {
            throw new BizException("申请不存在");
        }
        if (!PENDING.equals(app.getStatus())) {
            throw new BizException("仅待审核状态的申请可撤销");
        }
        // 归属校验以服务端登录态为准（不再信任客户端自报 studentId）：
        // 管理员可代撤；学生仅能撤销自己的申请
        String username = UserContext.getUsername();
        if (!"ADMIN".equals(UserContext.getRole()) && !app.getStudentId().equals(username)) {
            throw new BizException("无权操作该申请");
        }
        applyMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void direct(DirectCheckoutRequest req) {
        String remark = (req.getRemark() != null && !req.getRemark().isBlank())
                ? req.getRemark() : req.getReason();
        checkInService.checkout(req.getStudentId(), parseDateTime(req.getCheckoutDate()), "direct", remark);
    }

    // ---------- 汇总展示 ----------

    private CheckIn latestCheckIn(String studentId) {
        return checkInMapper.selectOne(Wrappers.<CheckIn>lambdaQuery()
                .eq(CheckIn::getStudentId, studentId).orderByDesc(CheckIn::getId).last("limit 1"));
    }

    private CheckoutAppVO toVO(CheckoutApply a, Student student, CheckIn latest) {
        CheckoutAppVO vo = new CheckoutAppVO();
        vo.setId(a.getId());
        vo.setApplyNo(a.getApplyNo());
        vo.setStudentId(a.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getName());
            vo.setCollege(student.getCollege());
        }
        if (latest != null) {
            vo.setBuildingName(latest.getBuildingName());
            vo.setRoomNo(latest.getRoomNo());
            vo.setBedNo(BedNoUtil.strip(latest.getBedNo()));
            vo.setRoomId(latest.getRoomId());
        }
        vo.setReason(a.getReason());
        vo.setPlanDate(a.getPlanDate());
        vo.setDescription(a.getDescription());
        vo.setStatus(a.getStatus());
        vo.setRejectReason(a.getRejectReason());
        vo.setCreateTime(a.getCreateTime());
        vo.setAuditTime(a.getAuditTime());
        return vo;
    }

    private static LocalDate parseLocalDate(String s) {
        if (s == null || s.isBlank()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDate.parse(s).atStartOfDay();
        } catch (DateTimeParseException e) {
            try {
                return LocalDateTime.parse(s);
            } catch (DateTimeParseException e2) {
                return LocalDateTime.now();
            }
        }
    }
}