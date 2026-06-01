package com.smarthospital.payment.strategy;

import lombok.Data;

@Data
public class RefundResult {
    private boolean success;
    private String refundTradeNo;
    private String failReason;

    public static RefundResult ok(String refundTradeNo) {
        RefundResult r = new RefundResult();
        r.setSuccess(true);
        r.setRefundTradeNo(refundTradeNo);
        return r;
    }

    public static RefundResult fail(String reason) {
        RefundResult r = new RefundResult();
        r.setSuccess(false);
        r.setFailReason(reason);
        return r;
    }
}
