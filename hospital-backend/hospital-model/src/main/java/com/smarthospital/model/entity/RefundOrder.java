package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("refund_order")
public class RefundOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String refundNo;
    private Long paymentOrderId;
    private Integer refundAmount;
    private String refundReason;
    private String status;
    private Long applicantId;
    private Long approverId;
    private LocalDateTime approveTime;
    private LocalDateTime refundTime;
    private String bizType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
