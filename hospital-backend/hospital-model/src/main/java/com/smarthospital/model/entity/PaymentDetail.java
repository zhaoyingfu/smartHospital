package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("payment_detail")
public class PaymentDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long paymentOrderId;
    private String payMethod;
    private Integer amount;
    private String tradeNo;
    private String status;
    private String failReason;
    private LocalDateTime payTime;
    private LocalDateTime callbackTime;
}
