package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("kiosk_heartbeat")
public class KioskHeartbeat {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long kioskId;
    private LocalDateTime heartbeatTime;
    private Integer paperRemaining;
}
