package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("patient_blacklist")
public class PatientBlacklist {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long patientId;
    private String reason;
    private Long operatorId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
