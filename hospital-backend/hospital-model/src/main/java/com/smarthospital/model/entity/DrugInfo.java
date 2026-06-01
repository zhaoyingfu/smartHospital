package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("drug_info")
public class DrugInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String pinYin;
    private String spec;
    private Integer unitPrice;
    private String category;
    private Integer status;
    private LocalDateTime hisSyncTime;
}
