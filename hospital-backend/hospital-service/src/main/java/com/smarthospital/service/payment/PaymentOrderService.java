package com.smarthospital.service.payment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.utils.TradeNoUtil;
import com.smarthospital.dal.mapper.PaymentDetailMapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.model.dto.PayReq;
import com.smarthospital.model.dto.TransactionQuery;
import com.smarthospital.model.entity.PaymentDetail;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.enums.PaymentStatus;
import com.smarthospital.payment.strategy.PayResult;
import com.smarthospital.payment.strategy.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrderService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final PaymentDetailMapper paymentDetailMapper;
    private final Map<String, PaymentStrategy> strategyMap;

    public PaymentOrder findById(Long id) {
        return paymentOrderMapper.selectById(id);
    }

    public PaymentOrder findByTradeNo(String outTradeNo) {
        return paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>().eq(PaymentOrder::getOutTradeNo, outTradeNo));
    }

    public List<PaymentDetail> findDetailsByOrderId(Long paymentOrderId) {
        return paymentDetailMapper.selectList(
                new LambdaQueryWrapper<PaymentDetail>().eq(PaymentDetail::getPaymentOrderId, paymentOrderId));
    }

    public Page<PaymentOrder> pageQuery(TransactionQuery query) {
        Page<PaymentOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<PaymentOrder> wrapper = new LambdaQueryWrapper<PaymentOrder>();
        if (StringUtils.hasText(query.getBizType())) {
            wrapper.eq(PaymentOrder::getBizType, query.getBizType());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(PaymentOrder::getStatus, query.getStatus());
        }
        if (query.getKioskId() != null) {
            wrapper.eq(PaymentOrder::getKioskId, query.getKioskId());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge(PaymentOrder::getCreateTime, query.getStartDate());
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le(PaymentOrder::getCreateTime, query.getEndDate());
        }
        wrapper.orderByDesc(PaymentOrder::getCreateTime);
        return paymentOrderMapper.selectPage(page, wrapper);
    }

    @Transactional
    public PaymentDetail processPayment(PayReq req) {
        PaymentOrder order = paymentOrderMapper.selectById(req.getPaymentOrderId());
        if (order == null) {
            throw new BizException("支付订单不存在");
        }
        if (!PaymentStatus.PENDING.name().equals(order.getStatus())) {
            throw new BizException("订单状态不允许支付");
        }
        if (order.getExpireTime().isBefore(LocalDateTime.now())) {
            order.setStatus(PaymentStatus.CLOSED.name());
            paymentOrderMapper.updateById(order);
            throw new BizException("支付已超时，请重新下单");
        }

        order.setStatus(PaymentStatus.PROCESSING.name());
        paymentOrderMapper.updateById(order);

        PaymentStrategy strategy = resolveStrategy(req.getPayMethod());
        int payAmount = order.getSelfAmount() > 0 ? order.getSelfAmount() : order.getTotalAmount();
        PayResult payResult = strategy.createPayment(
                order.getOutTradeNo(),
                BigDecimal.valueOf(payAmount).divide(BigDecimal.valueOf(100)),
                "智慧医院-" + order.getBizType());

        PaymentDetail detail = new PaymentDetail();
        detail.setPaymentOrderId(order.getId());
        detail.setPayMethod(req.getPayMethod());
        detail.setAmount(payAmount);
        detail.setTradeNo(payResult.getTradeNo());

        if (payResult.isSuccess()) {
            detail.setStatus("SUCCESS");
            detail.setPayTime(LocalDateTime.now());
            detail.setCallbackTime(LocalDateTime.now());

            PaymentDetail existingDetail = paymentDetailMapper.selectOne(
                    new LambdaQueryWrapper<PaymentDetail>()
                            .eq(PaymentDetail::getPaymentOrderId, order.getId())
                            .eq(PaymentDetail::getStatus, "SUCCESS"));
            order.setStatus(PaymentStatus.SUCCESS.name());
            paymentOrderMapper.updateById(order);
        } else {
            detail.setStatus("FAILED");
            detail.setFailReason(payResult.getFailReason());
            order.setStatus(PaymentStatus.FAILED.name());
            paymentOrderMapper.updateById(order);
        }

        paymentDetailMapper.insert(detail);
        return detail;
    }

    private PaymentStrategy resolveStrategy(String payMethod) {
        for (PaymentStrategy strategy : strategyMap.values()) {
            if (strategy.getPayMethod().equals(payMethod)) {
                return strategy;
            }
        }
        throw new BizException("不支持的支付方式: " + payMethod);
    }

    @Transactional
    public void closeExpiredOrders() {
        List<PaymentOrder> expired = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, PaymentStatus.PENDING.name())
                        .lt(PaymentOrder::getExpireTime, LocalDateTime.now()));
        for (PaymentOrder order : expired) {
            order.setStatus(PaymentStatus.CLOSED.name());
            paymentOrderMapper.updateById(order);
            log.info("关闭超时订单: {}", order.getOutTradeNo());
        }
    }
}