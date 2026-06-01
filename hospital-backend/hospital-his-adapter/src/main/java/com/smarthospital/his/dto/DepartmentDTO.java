package com.smarthospital.his.dto;

import lombok.Data;

@Data
public class DepartmentDTO {
    private String hisDeptId;
    private String name;
    private String code;
    private String category;
    private Integer sortOrder;
}
