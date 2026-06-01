package com.smarthospital.service.payment.refund;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.utils.TradeNoUtil;
import com.smarthospital.dal.mapper.PaymentDetailMapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.dal.mapper.RefundOrderMapper;
import com.smarthospital.model.dto.RefundApproveReq;
import com.smarthospital.model.entity.PaymentDetail;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.entity.RefundOrder;
import com.smarthospital.model.enums.PaymentStatus;
import com.smarthospital.model.enums.RefundStatus;
import com.smarthospital.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundOrderMapper refundOrderMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentDetailMapper paymentDetailMapper;
    private final Map<String, PaymentStrategy> strategyMap;

    @Transactional
    public RefundOrder createRefund(Long paymentOrderId, Integer refundAmount, String reason, String bizType, Long applicantId) {
        PaymentOrder payOrder = paymentOrderMapper.selectById(paymentOrderId);
        if (payOrder == null) {
            throw new BizException("支付订单不存在");
        }
        if (!PaymentStatus.SUCCESS.name().equals(payOrder.getStatus())) {
            throw new BizException("支付未成功，无法退款");
        }

        RefundOrder refund = new RefundOrder();
        refund.setRefundNo(TradeNoUtil.refundNo());
        refund.setPaymentOrderId(paymentOrderId);
        refund.setRefundAmount(refundAmount);
        refund.setRefundReason(reason);
        refund.setBizType(bizType);
        refund.setApplicantId(applicantId);

        if (refundAmount > 20000) {
            refund.setStatus(RefundStatus.PENDING_APPROVE.name());
        } else {
            refund.setStatus(RefundStatus.APPROVED.name());
        }

        refundOrderMapper.insert(refund);
        if (RefundStatus.APPROVED.name().equals(refund.getStatus())) {
            executeRefund(refund);
        }
        return refund;
    }

    @Transactional
    public void approve(RefundApproveReq req) {
        RefundOrder refund = refundOrderMapper.selectById(req.getRefundId());
        if (refund == null) {
            throw new BizException("退款单不存在");
        }
        if (!RefundStatus.PENDING_APPROVE.name().equals(refund.getStatus())) {
            throw new BizException("退款单状态不允许审批");
        }
        if (req.getApproved()) {
            refund.setStatus(RefundStatus.APPROVED.name());
            refund.setApproverId(req.getApproverId());
            refund.setApproveTime(LocalDateTime.now());
            refundOrderMapper.updateById(refund);
            executeRefund(refund);
        } else {
            refund.setStatus(RefundStatus.FAILED.name());
            refund.setApproverId(req.getApproverId());
            refund.setApproveTime(LocalDateTime.now());
            refundOrderMapper.updateById(refund);
        }
    }

    private void executeRefund(RefundOrder refund) {
        PaymentOrder payOrder = paymentOrderMapper.selectById(refund.getPaymentOrderId());
        List<PaymentDetail> details = paymentDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentDetail>()
                        .eq(PaymentDetail::getPaymentOrderId, payOrder.getId())
                        .eq(PaymentDetail::getStatus, "SUCCESS"));

        refund.setStatus(RefundStatus.REFUNDING.name());
        refundOrderMapper.updateById(refund);

        for (PaymentDetail detail : details) {
            PaymentStrategy strategy = resolveStrategy(detail.getPayMethod());
            var result = strategy.refund(
                    payOrder.getOutTradeNo(),
                    refund.getRefundNo(),
                    BigDecimal.valueOf(detail.getAmount()).divide(BigDecimal.valueOf(100)));
            if (!result.isSuccess()) {
                refund.setStatus(RefundStatus.FAILED.name());
                refundOrderMapper.updateById(refund);
                return;
            }
        }

        refund.setStatus(RefundStatus.REFUNDED.name());
        refund.setRefundTime(LocalDateTime.now());
        refundOrderMapper.updateById(refund);

        payOrder.setStatus(PaymentStatus.REFUNDED.name());
        paymentOrderMapper.updateById(payOrder);
    }

    private PaymentStrategy resolveStrategy(String payMethod) {
        for (PaymentStrategy strategy : strategyMap.values()) {
            if (strategy.getPayMethod().equals(payMethod)) {
                return strategy;
            }
        }
        throw new BizException("不支持的支付方式: " + payMethod);
    }

    public Page<RefundOrder> pageQuery(int pageNum, int pageSize) {
        Page<RefundOrder> page = new Page<>(pageNum, pageSize);
        return refundOrderMapper.selectPage(page,
                new LambdaQueryWrapper<RefundOrder>().orderByDesc(RefundOrder::getCreateTime));
    }
}