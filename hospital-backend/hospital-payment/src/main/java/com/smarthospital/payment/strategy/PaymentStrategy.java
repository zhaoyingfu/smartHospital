package com.smarthospital.payment.strategy;

import java.math.BigDecimal;

public interface PaymentStrategy {
    String getPayMethod();
    PayResult createPayment(String outTradeNo, BigDecimal amount, String description);
    PayResult queryPayment(String outTradeNo);
    RefundResult refund(String outTradeNo, String refundNo, BigDecimal amount);
}
