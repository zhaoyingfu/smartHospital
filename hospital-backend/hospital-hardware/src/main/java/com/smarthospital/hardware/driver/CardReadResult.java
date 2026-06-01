package com.smarthospital.hardware.driver;

import lombok.Data;

@Data
public class CardReadResult {
    private boolean success;
    private String cardNo;
    private String name;
    private String errorMsg;

    public static CardReadResult ok(String cardNo, String name) {
        CardReadResult r = new CardReadResult();
        r.setSuccess(true);
        r.setCardNo(cardNo);
        r.setName(name);
        return r;
    }

    public static CardReadResult fail(String errorMsg) {
        CardReadResult r = new CardReadResult();
        r.setSuccess(false);
        r.setErrorMsg(errorMsg);
        return r;
    }
}
