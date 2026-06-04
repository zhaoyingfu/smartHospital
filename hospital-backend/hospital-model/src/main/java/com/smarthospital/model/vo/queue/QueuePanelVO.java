package com.smarthospital.model.vo.queue;

import lombok.Data;
import java.util.List;

@Data
public class QueuePanelVO {
    private Long departmentId;
    private Long doctorId;
    private QueueVO currentCalling;
    private List<QueueVO> waitingList;
    private Long totalWaiting;
}
