package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("payment_order")
public class PaymentOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String outTradeNo;
    private String bizType;
    private Long bizId;
    private String bizNo;
    private Integer totalAmount;
    private Integer insuranceAmount;
    private Integer selfAmount;
    private String status;
    private LocalDateTime expireTime;
    private Long kioskId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
