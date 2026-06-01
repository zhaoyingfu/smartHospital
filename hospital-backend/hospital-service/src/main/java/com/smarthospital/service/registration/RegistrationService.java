package com.smarthospital.service.registration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.utils.TradeNoUtil;
import com.smarthospital.dal.mapper.*;
import com.smarthospital.his.client.HisClient;
import com.smarthospital.model.dto.RegistrationReq;
import com.smarthospital.model.entity.*;
import com.smarthospital.model.enums.PaymentStatus;
import com.smarthospital.service.patient.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final ScheduleMapper scheduleMapper;
    private final ScheduleLockMapper scheduleLockMapper;
    private final PaymentOrderMapper paymentOrderMapper;
    private final PatientService patientService;
    private final HisClient hisClient;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public Registration register(RegistrationReq req) {
        if (patientService.isBlacklisted(req.getPatientId())) {
            throw new BizException(403, "您已被限制自助挂号，请到窗口办理");
        }

        Long todayRegCount = registrationMapper.selectCount(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getPatientId, req.getPatientId())
                        .eq(Registration::getRegDate, LocalDateTime.now().toLocalDate()));
        if (todayRegCount != null && todayRegCount >= 3) {
            throw new BizException("今日挂号次数已达上限");
        }

        String lockKey = "schedule:lock:" + req.getScheduleId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(req.getPatientId()), 5, TimeUnit.MINUTES);
        if (locked == null || !locked) {
            throw new BizException("该号源正在被其他患者选择，请稍后重试");
        }

        Schedule schedule = scheduleMapper.selectById(req.getScheduleId());
        if (schedule == null || !"AVAILABLE".equals(schedule.getStatus())) {
            releaseLock(lockKey);
            throw new BizException("号源不可用");
        }
        if (schedule.getAvailableQuota() <= 0) {
            releaseLock(lockKey);
            throw new BizException("号源已满");
        }

        schedule.setAvailableQuota(schedule.getAvailableQuota() - 1);
        if (schedule.getAvailableQuota() <= 0) {
            schedule.setStatus("FULL");
        }
        scheduleMapper.updateById(schedule);

        ScheduleLock lockRecord = new ScheduleLock();
        lockRecord.setScheduleId(req.getScheduleId());
        lockRecord.setPatientId(req.getPatientId());
        lockRecord.setLockTime(LocalDateTime.now());
        lockRecord.setExpireTime(LocalDateTime.now().plusMinutes(5));
        lockRecord.setStatus("LOCKED");
        scheduleLockMapper.insert(lockRecord);

        Registration reg = new Registration();
        reg.setRegNo(TradeNoUtil.regNo());
        reg.setPatientId(req.getPatientId());
        reg.setScheduleId(req.getScheduleId());
        reg.setDoctorId(schedule.getDoctorId());
        reg.setDepartmentId(schedule.getDepartmentId());
        reg.setRegDate(schedule.getScheduleDate());
        reg.setTimePeriod(schedule.getTimePeriod());
        reg.setRegFee(schedule.getRegFee());
        reg.setTreatFee(schedule.getTreatFee());
        reg.setTotalFee(schedule.getRegFee() + schedule.getTreatFee());
        reg.setRegType(req.getRegType());
        reg.setStatus("REGISTERED");
        reg.setKioskId(req.getKioskId());
        registrationMapper.insert(reg);

        PaymentOrder payOrder = new PaymentOrder();
        payOrder.setOutTradeNo(TradeNoUtil.paymentTradeNo());
        payOrder.setBizType("REGISTRATION");
        payOrder.setBizId(reg.getId());
        payOrder.setBizNo(reg.getRegNo());
        payOrder.setTotalAmount(reg.getTotalFee());
        payOrder.setInsuranceAmount(0);
        payOrder.setSelfAmount(reg.getTotalFee());
        payOrder.setStatus(PaymentStatus.PENDING.name());
        payOrder.setExpireTime(LocalDateTime.now().plusMinutes(5));
        payOrder.setKioskId(req.getKioskId());
        paymentOrderMapper.insert(payOrder);

        reg.setPaymentOrderId(payOrder.getId());
        return reg;
    }

    public List<Registration> queryByPatientId(Long patientId) {
        return registrationMapper.selectList(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getPatientId, patientId)
                        .orderByDesc(Registration::getCreateTime));
    }

    public Registration findById(Long id) {
        return registrationMapper.selectById(id);
    }

    @Transactional
    public void cancelRegistration(Long registrationId, Long operatorId) {
        Registration reg = registrationMapper.selectById(registrationId);
        if (reg == null) {
            throw new BizException("挂号记录不存在");
        }
        if ("REFUNDED".equals(reg.getStatus())) {
            throw new BizException("该挂号已退号");
        }
        reg.setStatus("REFUNDED");
        registrationMapper.updateById(reg);

        Schedule schedule = scheduleMapper.selectById(reg.getScheduleId());
        if (schedule != null) {
            schedule.setAvailableQuota(schedule.getAvailableQuota() + 1);
            schedule.setStatus("AVAILABLE");
            scheduleMapper.updateById(schedule);
        }

        if (reg.getHisOrderNo() != null) {
            hisClient.cancelRegistration(reg.getHisOrderNo());
        }
    }

    private void releaseLock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}