package com.smarthospital.his.dto;

import lombok.Data;

@Data
public class OutpatientItemDTO {
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private Integer unitPrice;
    private Integer amount;
}
