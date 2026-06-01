package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("reconciliation_record")
public class ReconciliationRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate reconDate;
    private String channel;
    private Integer totalCount;
    private Integer matchedCount;
    private Integer longCount;
    private Integer shortCount;
    private Integer amountDiffCount;
    private String status;
    private String reconFileUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
