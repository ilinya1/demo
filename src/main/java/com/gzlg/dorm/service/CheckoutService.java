package com.gzlg.dorm.service;

import com.gzlg.dorm.common.result.PageResult;
import com.gzlg.dorm.dto.AuditRequest;
import com.gzlg.dorm.dto.CheckoutApplyRequest;
import com.gzlg.dorm.dto.DirectCheckoutRequest;
import com.gzlg.dorm.vo.CheckoutAppVO;

/**
 * 退宿业务服务（申请/审核/直接退宿/撤销）。
 */
public interface CheckoutService {

    PageResult<CheckoutAppVO> listApps(String applyNo, String studentId, String status, int page, int pageSize);

    void submitApply(CheckoutApplyRequest req);

    void audit(Long id, AuditRequest req);

    void cancel(Long id);

    void direct(DirectCheckoutRequest req);
}