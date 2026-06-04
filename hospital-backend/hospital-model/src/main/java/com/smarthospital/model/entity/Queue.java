package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("queue")
public class Queue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long registrationId;
    private Long departmentId;
    private Long doctorId;
    private String queueNo;
    private String status;
    private LocalDateTime callTime;
    private Integer waitCount;
    private Integer callCount;
    private String type;
    private Long revisitSourceId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
