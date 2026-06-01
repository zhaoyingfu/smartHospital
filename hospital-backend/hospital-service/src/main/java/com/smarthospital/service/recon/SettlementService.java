package com.smarthospital.service.recon;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.dal.mapper.DailySettlementMapper;
import com.smarthospital.dal.mapper.PaymentDetailMapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.dal.mapper.RefundOrderMapper;
import com.smarthospital.model.entity.DailySettlement;
import com.smarthospital.model.entity.PaymentDetail;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.entity.RefundOrder;
import com.smarthospital.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final DailySettlementMapper settlementMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentDetailMapper paymentDetailMapper;
    private final RefundOrderMapper refundOrderMapper;

    @Transactional
    public DailySettlement generateDaily(LocalDate settleDate) {
        DailySettlement existing = settlementMapper.selectOne(
                new LambdaQueryWrapper<DailySettlement>().eq(DailySettlement::getSettleDate, settleDate));
        if (existing != null) {
            throw new BizException("该日期已有结算记录");
        }

        LocalDateTime startTime = settleDate.atStartOfDay();
        LocalDateTime endTime = settleDate.atTime(LocalTime.MAX);

        List<PaymentOrder> successOrders = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, PaymentStatus.SUCCESS.name())
                        .between(PaymentOrder::getCreateTime, startTime, endTime));

        List<PaymentDetail> details = paymentDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentDetail>()
                        .eq(PaymentDetail::getStatus, "SUCCESS")
                        .between(PaymentDetail::getPayTime, startTime, endTime));

        int wechatAmount = 0, alipayAmount = 0, bankAmount = 0, medicareAmount = 0;
        for (PaymentDetail d : details) {
            switch (d.getPayMethod()) {
                case "WECHAT" -> wechatAmount += d.getAmount();
                case "ALIPAY" -> alipayAmount += d.getAmount();
                case "BANK_CARD" -> bankAmount += d.getAmount();
                case "MEDICARE" -> medicareAmount += d.getAmount();
            }
        }

        List<RefundOrder> refunds = refundOrderMapper.selectList(
                new LambdaQueryWrapper<RefundOrder>()
                        .eq(RefundOrder::getStatus, "REFUNDED")
                        .between(RefundOrder::getRefundTime, startTime, endTime));
        int totalRefund = refunds.stream().mapToInt(RefundOrder::getRefundAmount).sum();

        DailySettlement settlement = new DailySettlement();
        settlement.setSettleDate(settleDate);
        settlement.setTotalTransaction(successOrders.size());
        settlement.setTotalAmount(successOrders.stream().mapToInt(PaymentOrder::getTotalAmount).sum());
        settlement.setWechatAmount(wechatAmount);
        settlement.setAlipayAmount(alipayAmount);
        settlement.setBankAmount(bankAmount);
        settlement.setMedicareAmount(medicareAmount);
        settlement.setTotalRefund(totalRefund);
        settlement.setReconStatus("PENDING");
        settlement.setStatus("PENDING");
        settlementMapper.insert(settlement);

        return settlement;
    }

    @Transactional
    public void confirm(Long settlementId, Long confirmerId) {
        DailySettlement s = settlementMapper.selectById(settlementId);
        if (s == null) {
            throw new BizException("结算记录不存在");
        }
        if (!"PENDING".equals(s.getStatus()) && !"CONFIRMED".equals(s.getStatus())) {
            throw new BizException("结算记录状态不允许确认");
        }
        s.setStatus("CONFIRMED");
        s.setConfirmerId(confirmerId);
        s.setConfirmTime(LocalDateTime.now());
        settlementMapper.updateById(s);
    }

    @Transactional
    public void lock(Long settlementId) {
        DailySettlement s = settlementMapper.selectById(settlementId);
        if (s == null) {
            throw new BizException("结算记录不存在");
        }
        if (!"CONFIRMED".equals(s.getStatus())) {
            throw new BizException("需先确认结算才能锁定");
        }
        s.setStatus("LOCKED");
        settlementMapper.updateById(s);
    }

    public Page<DailySettlement> pageQuery(int pageNum, int pageSize) {
        Page<DailySettlement> page = new Page<>(pageNum, pageSize);
        return settlementMapper.selectPage(page,
                new LambdaQueryWrapper<DailySettlement>().orderByDesc(DailySettlement::getSettleDate));
    }
}