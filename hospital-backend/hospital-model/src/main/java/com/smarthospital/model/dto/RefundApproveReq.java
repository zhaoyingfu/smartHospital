package com.smarthospital.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefundApproveReq {
    @NotNull(message = "退款单ID不能为空")
    private Long refundId;
    @NotNull(message = "审批结果不能为空")
    private Boolean approved;
    private String reason;
    private Long approverId;
}
