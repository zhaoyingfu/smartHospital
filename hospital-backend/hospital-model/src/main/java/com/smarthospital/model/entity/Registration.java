package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("registration")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String regNo;
    private Long patientId;
    private Long scheduleId;
    private Long doctorId;
    private Long departmentId;
    private LocalDate regDate;
    private String timePeriod;
    private Integer regFee;
    private Integer treatFee;
    private Integer totalFee;
    private String regType;
    private String status;
    private LocalDateTime signTime;
    private String queueNo;
    private String hisOrderNo;
    private Long kioskId;
    @TableField(exist = false)
    private Long paymentOrderId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
