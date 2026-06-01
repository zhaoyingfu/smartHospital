package com.smarthospital.job;

import com.smarthospital.service.kiosk.KioskMonitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KioskOfflineDetectJob {

    private final KioskMonitorService kioskMonitorService;

    @Scheduled(fixedRate = 60000)
    public void detect() {
        kioskMonitorService.detectOffline();
    }
}