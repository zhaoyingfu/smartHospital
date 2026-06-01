package com.smarthospital.his.dto;

import lombok.Data;

@Data
public class PatientDTO {
    private String hisPatientId;
    private String name;
    private String idCard;
    private String phone;
    private String medicareCard;
    private String medicalNo;
    private Boolean exists;
}
