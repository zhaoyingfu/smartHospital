package com.smarthospital.job;

import com.smarthospital.service.anomaly.AnomalyDetectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnomalyDetectJob {

    private final AnomalyDetectService anomalyDetectService;

    @Scheduled(fixedRate = 300000)
    public void detect() {
        List<String> anomalies = anomalyDetectService.detectAnomalies();
        if (!anomalies.isEmpty()) {
            log.warn("检测到异常交易: {}", anomalies);
        }
    }
}