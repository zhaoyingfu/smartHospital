package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("schedule")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long doctorId;
    private Long departmentId;
    private LocalDate scheduleDate;
    private String timePeriod;
    private Integer totalQuota;
    private Integer availableQuota;
    private Integer regFee;
    private Integer treatFee;
    private String status;
    private LocalDateTime hisSyncTime;
}
