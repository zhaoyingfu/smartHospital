package com.smarthospital.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InpatientDepositReq {
    @NotNull(message = "患者ID不能为空")
    private Long patientId;
    @NotNull(message = "住院号不能为空")
    private String inpatientNo;
    @NotNull(message = "充值金额不能为空")
    @Positive(message = "充值金额必须大于0")
    private Integer amount;
    private Long kioskId;
}
