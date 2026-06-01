package com.smarthospital.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("outpatient_order_item")
public class OutpatientOrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long outpatientOrderId;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
    private String hisItemCode;
}
