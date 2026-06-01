package com.smarthospital.payment.strategy;

import lombok.Data;

@Data
public class PayResult {
    private boolean success;
    private String tradeNo;
    private String qrCode;
    private String failReason;

    public static PayResult ok(String tradeNo) {
        PayResult r = new PayResult();
        r.setSuccess(true);
        r.setTradeNo(tradeNo);
        return r;
    }

    public static PayResult qrcode(String tradeNo, String qrCode) {
        PayResult r = new PayResult();
        r.setSuccess(true);
        r.setTradeNo(tradeNo);
        r.setQrCode(qrCode);
        return r;
    }

    public static PayResult fail(String reason) {
        PayResult r = new PayResult();
        r.setSuccess(false);
        r.setFailReason(reason);
        return r;
    }
}
