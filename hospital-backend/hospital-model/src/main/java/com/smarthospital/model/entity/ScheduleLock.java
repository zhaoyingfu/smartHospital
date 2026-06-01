package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("schedule_lock")
public class ScheduleLock {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private Long patientId;
    private LocalDateTime lockTime;
    private LocalDateTime expireTime;
    private String status;
}
