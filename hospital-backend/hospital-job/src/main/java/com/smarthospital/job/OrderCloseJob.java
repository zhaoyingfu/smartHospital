package com.smarthospital.job;

import com.smarthospital.service.payment.PaymentOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCloseJob {

    private final PaymentOrderService paymentOrderService;

    @Scheduled(fixedRate = 60000)
    public void closeExpiredOrders() {
        paymentOrderService.closeExpiredOrders();
    }
}