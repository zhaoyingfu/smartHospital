package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reconciliation_ticket")
public class ReconciliationTicket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reconciliationDetailId;
    private String title;
    private String description;
    private String status;
    private Long assigneeId;
    private String resolution;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
