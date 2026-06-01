package com.smarthospital.service.anomaly;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.dal.mapper.RegistrationMapper;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.entity.Registration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnomalyDetectService {

    private final RegistrationMapper registrationMapper;
    private final PaymentOrderMapper paymentOrderMapper;

    public List<String> detectAnomalies() {
        List<String> anomalies = new ArrayList<>();
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        Long frequentReg = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .ge(Registration::getCreateTime, oneHourAgo)
                        .groupBy(Registration::getPatientId)
                        .having("COUNT(*) > 5")
                        .last("LIMIT 1"));
        if (frequentReg != null && frequentReg > 0L) {
            anomalies.add("检测到短时间频繁挂号行为");
        }

        Long frequentRefund = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getStatus, "REFUNDED")
                        .ge(Registration::getUpdateTime, oneHourAgo));
        if (frequentRefund != null && frequentRefund > 3L) {
            anomalies.add("检测到短时间内多笔退号");
        }

        List<PaymentOrder> largePayments = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .ge(PaymentOrder::getCreateTime, oneHourAgo)
                        .gt(PaymentOrder::getTotalAmount, 500000));
        if (!largePayments.isEmpty()) {
            anomalies.add("检测到大额交易，共" + largePayments.size() + "笔");
        }

        return anomalies;
    }
}