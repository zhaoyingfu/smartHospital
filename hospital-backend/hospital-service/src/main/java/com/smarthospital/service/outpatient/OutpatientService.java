package com.smarthospital.service.outpatient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.utils.TradeNoUtil;
import com.smarthospital.dal.mapper.OutpatientOrderItemMapper;
import com.smarthospital.dal.mapper.OutpatientOrderMapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.his.client.HisClient;
import com.smarthospital.his.dto.OutpatientItemDTO;
import com.smarthospital.his.dto.OutpatientOrderDTO;
import com.smarthospital.model.dto.OutpatientPayReq;
import com.smarthospital.model.entity.OutpatientOrder;
import com.smarthospital.model.entity.OutpatientOrderItem;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutpatientService {

    private final OutpatientOrderMapper outpatientOrderMapper;
    private final OutpatientOrderItemMapper outpatientOrderItemMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final HisClient hisClient;

    @Transactional
    public OutpatientOrder createFromHis(Long patientId, String visitNo, Long kioskId) {
        List<OutpatientOrderDTO> hisOrders = hisClient.queryOutpatientOrders(patientId, visitNo);
        if (hisOrders == null || hisOrders.isEmpty()) {
            throw new BizException("未查询到待缴费的门诊订单");
        }

        OutpatientOrderDTO hisOrder = hisOrders.get(0);
        OutpatientOrder order = new OutpatientOrder();
        order.setOrderNo(TradeNoUtil.orderNo());
        order.setPatientId(patientId);
        order.setVisitNo(visitNo);
        order.setTotalAmount(hisOrder.getTotalAmount());
        order.setStatus("UNPAID");
        order.setHisOrderNo(hisOrder.getHisOrderNo());
        order.setKioskId(kioskId);
        outpatientOrderMapper.insert(order);

        if (hisOrder.getItems() != null) {
            for (OutpatientItemDTO item : hisOrder.getItems()) {
                OutpatientOrderItem oi = new OutpatientOrderItem();
                oi.setOutpatientOrderId(order.getId());
                oi.setItemName(item.getItemName());
                oi.setItemCode(item.getItemCode());
                oi.setQuantity(item.getQuantity());
                oi.setUnitPrice(item.getUnitPrice());
                oi.setAmount(item.getAmount());
                outpatientOrderItemMapper.insert(oi);
            }
        }

        PaymentOrder payOrder = new PaymentOrder();
        payOrder.setOutTradeNo(TradeNoUtil.paymentTradeNo());
        payOrder.setBizType("OUTPATIENT");
        payOrder.setBizId(order.getId());
        payOrder.setBizNo(order.getOrderNo());
        payOrder.setTotalAmount(order.getTotalAmount());
        payOrder.setInsuranceAmount(0);
        payOrder.setSelfAmount(order.getTotalAmount());
        payOrder.setStatus(PaymentStatus.PENDING.name());
        payOrder.setExpireTime(LocalDateTime.now().plusMinutes(5));
        payOrder.setKioskId(kioskId);
        paymentOrderMapper.insert(payOrder);

        return order;
    }

    public List<OutpatientOrder> queryByPatientId(Long patientId) {
        return outpatientOrderMapper.selectList(
                new LambdaQueryWrapper<OutpatientOrder>()
                        .eq(OutpatientOrder::getPatientId, patientId)
                        .orderByDesc(OutpatientOrder::getCreateTime));
    }

    public List<OutpatientOrderItem> queryItems(Long orderId) {
        return outpatientOrderItemMapper.selectList(
                new LambdaQueryWrapper<OutpatientOrderItem>().eq(OutpatientOrderItem::getOutpatientOrderId, orderId));
    }
}