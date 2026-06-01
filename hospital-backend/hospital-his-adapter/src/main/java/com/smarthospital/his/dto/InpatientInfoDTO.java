package com.smarthospital.his.dto;

import lombok.Data;

@Data
public class InpatientInfoDTO {
    private String inpatientNo;
    private String patientName;
    private String departmentName;
    private String bedNo;
    private Integer balance;
}
