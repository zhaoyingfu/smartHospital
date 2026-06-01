package com.smarthospital.payment.impl;

import com.smarthospital.payment.strategy.PayResult;
import com.smarthospital.payment.strategy.PaymentStrategy;
import com.smarthospital.payment.strategy.RefundResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class MedicareStrategy implements PaymentStrategy {

    @Override
    public String getPayMethod() {
        return "MEDICARE";
    }

    @Override
    public PayResult createPayment(String outTradeNo, BigDecimal amount, String description) {
        log.info("[模拟医保支付] 创建支付: outTradeNo={}, amount={}", outTradeNo, amount);
        String tradeNo = "MED" + System.currentTimeMillis();
        return PayResult.ok(tradeNo);
    }

    @Override
    public PayResult queryPayment(String outTradeNo) {
        return PayResult.ok("MED" + System.currentTimeMillis());
    }

    @Override
    public RefundResult refund(String outTradeNo, String refundNo, BigDecimal amount) {
        log.info("[模拟医保退款] outTradeNo={}, refundNo={}, amount={}", outTradeNo, refundNo, amount);
        return RefundResult.ok("MED_REFUND" + System.currentTimeMillis());
    }
}
