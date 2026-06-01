package com.smarthospital.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PayReq {
    @NotNull(message = "支付订单ID不能为空")
    private Long paymentOrderId;
    @NotNull(message = "支付方式不能为空")
    private String payMethod;
    private Long kioskId;
}
