package com.smarthospital.his.dto;

import lombok.Data;

@Data
public class DrugDTO {
    private String hisDrugId;
    private String name;
    private String pinYin;
    private String spec;
    private Integer unitPrice;
    private String category;
}
