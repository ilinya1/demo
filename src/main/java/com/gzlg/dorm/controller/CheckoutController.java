package com.gzlg.dorm.controller;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.common.result.Result;
import com.gzlg.dorm.dto.AuditRequest;
import com.gzlg.dorm.dto.CheckoutApplyRequest;
import com.gzlg.dorm.dto.DirectCheckoutRequest;
import com.gzlg.dorm.service.CheckoutService;
import com.gzlg.dorm.vo.CheckoutAppVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 退宿业务接口（申请列表/提交/审核/撤销/直接退宿）。
 */
@RestController
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /** 退宿申请列表（管理员）；学生端传入 studentId 筛自己的申请 */
    @GetMapping("/checkout-applications")
    public Result<PageResult<CheckoutAppVO>> list(@RequestParam(required = false) String applyNo,
                                                  @RequestParam(required = false) String studentId,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(checkoutService.listApps(applyNo, studentId, status, page, pageSize));
    }

    /** 学生提交退宿申请 */
    @PostMapping("/checkout-applications")
    public Result<Void> submit(@RequestBody @Valid CheckoutApplyRequest req) {
        checkoutService.submitApply(req);
        return Result.ok();
    }

    /** 管理员审核 */
    @PostMapping("/checkout-applications/{id}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditRequest req) {
        checkoutService.audit(id, req);
        return Result.ok();
    }

    /** 撤销待审核申请：归属身份以服务端登录态为准（学生仅能撤自己的，管理员可代撤） */
    @PostMapping("/checkout-applications/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        checkoutService.cancel(id);
        return Result.ok();
    }

    /** 直接退宿（跳过申请） */
    @PostMapping("/checkout/direct")
    public Result<Void> direct(@RequestBody @Valid DirectCheckoutRequest req) {
        checkoutService.direct(req);
        return Result.ok();
    }
}