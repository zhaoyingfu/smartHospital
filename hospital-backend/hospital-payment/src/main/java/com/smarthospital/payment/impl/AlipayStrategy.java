package com.smarthospital.payment.impl;

import com.smarthospital.payment.strategy.PayResult;
import com.smarthospital.payment.strategy.PaymentStrategy;
import com.smarthospital.payment.strategy.RefundResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class AlipayStrategy implements PaymentStrategy {

    @Override
    public String getPayMethod() {
        return "ALIPAY";
    }

    @Override
    public PayResult createPayment(String outTradeNo, BigDecimal amount, String description) {
        log.info("[模拟支付宝支付] 创建支付: outTradeNo={}, amount={}", outTradeNo, amount);
        String tradeNo = "ALI" + System.currentTimeMillis();
        String qrCode = "https://qr.alipay.com/" + UUID.randomUUID().toString().substring(0, 12);
        return PayResult.qrcode(tradeNo, qrCode);
    }

    @Override
    public PayResult queryPayment(String outTradeNo) {
        log.info("[模拟支付宝支付] 查询支付: outTradeNo={}", outTradeNo);
        return PayResult.ok("ALI" + System.currentTimeMillis());
    }

    @Override
    public RefundResult refund(String outTradeNo, String refundNo, BigDecimal amount) {
        log.info("[模拟支付宝退款] outTradeNo={}, refundNo={}, amount={}", outTradeNo, refundNo, amount);
        return RefundResult.ok("ALI_REFUND" + System.currentTimeMillis());
    }
}
