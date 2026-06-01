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
public class WechatPayStrategy implements PaymentStrategy {

    @Override
    public String getPayMethod() {
        return "WECHAT";
    }

    @Override
    public PayResult createPayment(String outTradeNo, BigDecimal amount, String description) {
        log.info("[模拟微信支付] 创建支付: outTradeNo={}, amount={}", outTradeNo, amount);
        String tradeNo = "WX" + System.currentTimeMillis();
        String qrCode = "weixin://wxpay/bizpayurl?pr=" + UUID.randomUUID().toString().substring(0, 8);
        return PayResult.qrcode(tradeNo, qrCode);
    }

    @Override
    public PayResult queryPayment(String outTradeNo) {
        log.info("[模拟微信支付] 查询支付: outTradeNo={}", outTradeNo);
        return PayResult.ok("WX" + System.currentTimeMillis());
    }

    @Override
    public RefundResult refund(String outTradeNo, String refundNo, BigDecimal amount) {
        log.info("[模拟微信退款] outTradeNo={}, refundNo={}, amount={}", outTradeNo, refundNo, amount);
        return RefundResult.ok("WX_REFUND" + System.currentTimeMillis());
    }
}
