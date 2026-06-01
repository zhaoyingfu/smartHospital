package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("reconciliation_detail")
public class ReconciliationDetail {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long reconciliationRecordId;
    private String result;
    private String systemTradeNo;
    private Integer systemAmount;
    private String channelTradeNo;
    private Integer channelAmount;
    private Integer diffAmount;
    private String handleStatus;
    private String handleResult;
    private Long handlerId;
    private LocalDateTime handleTime;
}
