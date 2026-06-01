package com.smarthospital.his.dto;

import lombok.Data;

@Data
public class ScheduleDTO {
    private String hisScheduleId;
    private String hisDoctorId;
    private String departmentCode;
    private String scheduleDate;
    private String timePeriod;
    private Integer totalQuota;
    private Integer availableQuota;
    private Integer regFee;
    private Integer treatFee;
}
