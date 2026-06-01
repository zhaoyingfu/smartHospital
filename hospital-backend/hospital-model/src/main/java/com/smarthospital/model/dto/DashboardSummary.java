package com.smarthospital.model.dto;

import lombok.Data;

@Data
public class DashboardSummary {
    private long todayTransactionCount;
    private long todayTransactionAmount;
    private long todayRefundCount;
    private long onlineDeviceCount;
}
