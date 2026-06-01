package com.smarthospital.model.dto;

import lombok.Data;

@Data
public class SignInReq {
    private String regNo;
    private Long patientId;
    private Long kioskId;
}
