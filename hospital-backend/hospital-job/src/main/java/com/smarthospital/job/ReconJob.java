package com.smarthospital.job;

import com.smarthospital.service.recon.ReconService;
import com.smarthospital.service.recon.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReconJob {

    private final ReconService reconService;
    private final SettlementService settlementService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyRecon() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("开始T+1对账, 日期: {}", yesterday);
        try {
            reconService.executeRecon(yesterday, "WECHAT");
            reconService.executeRecon(yesterday, "ALIPAY");
            reconService.executeRecon(yesterday, "BANK_CARD");
            reconService.executeRecon(yesterday, "MEDICARE");
            reconService.executeRecon(yesterday, "HIS");
            settlementService.generateDaily(yesterday);
            log.info("T+1对账完成");
        } catch (Exception e) {
            log.error("T+1对账异常", e);
        }
    }
}