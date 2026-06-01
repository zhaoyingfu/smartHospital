package com.smarthospital.model.dto;

import lombok.Data;

@Data
public class IdentityVerifyReq {
    private String idCard;
    private String medicareCard;
    private String phone;
    private String verifyType;
}
