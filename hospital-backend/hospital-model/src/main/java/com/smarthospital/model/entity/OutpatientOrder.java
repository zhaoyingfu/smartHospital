package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("outpatient_order")
public class OutpatientOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;
    private Long patientId;
    private String visitNo;
    private Integer totalAmount;
    private String status;
    private String hisOrderNo;
    private Long kioskId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
