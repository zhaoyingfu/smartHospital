package com.smarthospital.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.RefundApproveReq;
import com.smarthospital.model.entity.RefundOrder;
import com.smarthospital.service.payment.refund.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/refund")
@RequiredArgsConstructor
public class AdminRefundController {

    private final RefundService refundService;

    @GetMapping("/list")
    public R<Page<RefundOrder>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return R.ok(refundService.pageQuery(pageNum, pageSize));
    }

    @PostMapping("/approve")
    public R<Void> approve(@Valid @RequestBody RefundApproveReq req) {
        refundService.approve(req);
        return R.ok();
    }
}