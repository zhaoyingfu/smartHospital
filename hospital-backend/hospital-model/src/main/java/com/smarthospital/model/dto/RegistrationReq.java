package com.smarthospital.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrationReq {
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    @NotNull(message = "排班ID不能为空")
    private Long scheduleId;
    @NotNull(message = "挂号类型不能为空")
    private String regType;
    private Long kioskId;
}
