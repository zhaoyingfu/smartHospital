package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("kiosk_machine")
public class KioskMachine {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String machineNo;
    private String location;
    private String status;
    private LocalDateTime lastHeartbeat;
    private String paperStatus;
    private String appVersion;
    private String features;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
