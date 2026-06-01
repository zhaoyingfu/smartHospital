package com.smarthospital.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OutpatientPayReq {
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    @NotNull(message = "就诊号不能为空")
    private String visitNo;
    private Long kioskId;
}
