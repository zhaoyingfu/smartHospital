package com.smarthospital.service.dashboard;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.dal.mapper.KioskMachineMapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.dal.mapper.RefundOrderMapper;
import com.smarthospital.model.dto.DashboardSummary;
import com.smarthospital.model.entity.KioskMachine;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.entity.RefundOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final RefundOrderMapper refundOrderMapper;
    private final KioskMachineMapper kioskMachineMapper;

    public DashboardSummary getSummary() {
        DashboardSummary summary = new DashboardSummary();

        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);

        LambdaQueryWrapper<PaymentOrder> payWrapper = new LambdaQueryWrapper<PaymentOrder>()
                .ge(PaymentOrder::getCreateTime, dayStart)
                .le(PaymentOrder::getCreateTime, dayEnd);
        summary.setTodayTransactionCount(paymentOrderMapper.selectCount(payWrapper));

        List<PaymentOrder> todayOrders = paymentOrderMapper.selectList(payWrapper);
        long totalAmount = todayOrders.stream()
                .filter(o -> "SUCCESS".equals(o.getStatus()))
                .mapToLong(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0L)
                .sum();
        summary.setTodayTransactionAmount(totalAmount);

        LambdaQueryWrapper<RefundOrder> refundWrapper = new LambdaQueryWrapper<RefundOrder>()
                .ge(RefundOrder::getCreateTime, dayStart)
                .le(RefundOrder::getCreateTime, dayEnd);
        summary.setTodayRefundCount(refundOrderMapper.selectCount(refundWrapper));

        LambdaQueryWrapper<KioskMachine> kioskWrapper = new LambdaQueryWrapper<KioskMachine>()
                .eq(KioskMachine::getStatus, "ONLINE");
        summary.setOnlineDeviceCount(kioskMachineMapper.selectCount(kioskWrapper));

        return summary;
    }
}
