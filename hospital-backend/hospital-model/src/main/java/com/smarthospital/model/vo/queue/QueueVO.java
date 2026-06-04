package com.smarthospital.model.vo.queue;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QueueVO {
    private Long id;
    private String queueNo;
    private String status;
    private Integer callCount;
    private String type;
    private Integer waitCount;
    private LocalDateTime callTime;
    private Long departmentId;
    private Long doctorId;
    private String regNo;
    private String patientName;
    private Integer rank;
}
