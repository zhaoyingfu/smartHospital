package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("inpatient_deposit")
public class InpatientDeposit {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String depositNo;
    private Long patientId;
    private String inpatientNo;
    private Integer amount;
    private Integer balanceBefore;
    private Integer balanceAfter;
    private String status;
    private String hisReceiptNo;
    private Long kioskId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
