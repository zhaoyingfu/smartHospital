package com.smarthospital.service.inpatient;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.utils.TradeNoUtil;
import com.smarthospital.dal.mapper.InpatientDepositMapper;
import com.smarthospital.dal.mapper.PaymentOrderMapper;
import com.smarthospital.his.client.HisClient;
import com.smarthospital.his.dto.InpatientInfoDTO;
import com.smarthospital.model.dto.InpatientDepositReq;
import com.smarthospital.model.entity.InpatientDeposit;
import com.smarthospital.model.entity.PaymentOrder;
import com.smarthospital.model.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InpatientService {

    private final InpatientDepositMapper inpatientDepositMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final HisClient hisClient;

    public InpatientInfoDTO queryInpatientInfo(String inpatientNo) {
        InpatientInfoDTO info = hisClient.queryInpatientInfo(inpatientNo);
        if (info == null) {
            throw new BizException("未查询到住院信息");
        }
        return info;
    }

    @Transactional
    public InpatientDeposit createDeposit(InpatientDepositReq req) {
        InpatientInfoDTO info = hisClient.queryInpatientInfo(req.getInpatientNo());

        InpatientDeposit deposit = new InpatientDeposit();
        deposit.setDepositNo(TradeNoUtil.depositNo());
        deposit.setPatientId(req.getPatientId());
        deposit.setInpatientNo(req.getInpatientNo());
        deposit.setAmount(req.getAmount());
        deposit.setBalanceBefore(info.getBalance());
        deposit.setBalanceAfter(info.getBalance() + req.getAmount());
        deposit.setStatus("UNPAID");
        deposit.setKioskId(req.getKioskId());
        inpatientDepositMapper.insert(deposit);

        PaymentOrder payOrder = new PaymentOrder();
        payOrder.setOutTradeNo(TradeNoUtil.paymentTradeNo());
        payOrder.setBizType("INPATIENT");
        payOrder.setBizId(deposit.getId());
        payOrder.setBizNo(deposit.getDepositNo());
        payOrder.setTotalAmount(req.getAmount());
        payOrder.setInsuranceAmount(0);
        payOrder.setSelfAmount(req.getAmount());
        payOrder.setStatus(PaymentStatus.PENDING.name());
        payOrder.setExpireTime(LocalDateTime.now().plusMinutes(5));
        payOrder.setKioskId(req.getKioskId());
        paymentOrderMapper.insert(payOrder);

        return deposit;
    }

    public List<InpatientDeposit> queryByPatientId(Long patientId) {
        return inpatientDepositMapper.selectList(
                new LambdaQueryWrapper<InpatientDeposit>()
                        .eq(InpatientDeposit::getPatientId, patientId)
                        .orderByDesc(InpatientDeposit::getCreateTime));
    }
}