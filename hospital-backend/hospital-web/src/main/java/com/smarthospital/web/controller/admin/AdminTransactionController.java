package com.smarthospital.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.response.R;
import com.smarthospital.model.dto.TransactionQuery;
import com.smarthospital.model.entity.PaymentDetail;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.service.payment.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/transaction")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final PaymentOrderService paymentOrderService;

    @GetMapping("/list")
    public R<Page<PaymentOrder>> list(TransactionQuery query) {
        return R.ok(paymentOrderService.pageQuery(query));
    }

    @GetMapping("/{id}")
    public R<PaymentOrder> detail(@PathVariable Long id) {
        return R.ok(paymentOrderService.findById(id));
    }

    @GetMapping("/{id}/details")
    public R<List<PaymentDetail>> paymentDetails(@PathVariable Long id) {
        return R.ok(paymentOrderService.findDetailsByOrderId(id));
    }
}