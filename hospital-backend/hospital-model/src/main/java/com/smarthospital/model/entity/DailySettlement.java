package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("daily_settlement")
public class DailySettlement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate settleDate;
    private Integer totalTransaction;
    private Integer totalAmount;
    private Integer wechatAmount;
    private Integer alipayAmount;
    private Integer bankAmount;
    private Integer medicareAmount;
    private Integer totalRefund;
    private String reconStatus;
    private String status;
    private Long confirmerId;
    private LocalDateTime confirmTime;
}
