package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("biz_rule")
public class BizRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleKey;
    private String ruleValue;
    private String description;
    private String scope;
    private Long scopeId;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
