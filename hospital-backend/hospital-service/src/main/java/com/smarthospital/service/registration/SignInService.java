package com.smarthospital.service.registration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.common.utils.TradeNoUtil;
import com.smarthospital.dal.mapper.QueueMapper;
import com.smarthospital.dal.mapper.RegistrationMapper;
import com.smarthospital.his.client.HisClient;
import com.smarthospital.model.entity.Queue;
import com.smarthospital.model.entity.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignInService {

    private final RegistrationMapper registrationMapper;
    private final QueueMapper queueMapper;
    private final HisClient hisClient;

    @Transactional
    public Queue signIn(Long patientId, String regNo) {
        LambdaQueryWrapper<Registration> query = new LambdaQueryWrapper<Registration>()
                .eq(Registration::getPatientId, patientId);
        if (regNo != null) {
            query.eq(Registration::getRegNo, regNo);
        }
        query.eq(Registration::getStatus, "REGISTERED")
             .orderByDesc(Registration::getCreateTime)
             .last("LIMIT 1");

        Registration reg = registrationMapper.selectOne(query);
        if (reg == null) {
            throw new BizException("未找到可签到的挂号记录");
        }
        reg.setStatus("SIGNED_IN");
        reg.setSignTime(LocalDateTime.now());
        registrationMapper.updateById(reg);

        int waitCount = Math.toIntExact(queueMapper.selectCount(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDepartmentId, reg.getDepartmentId())
                        .eq(Queue::getDoctorId, reg.getDoctorId())
                        .eq(Queue::getStatus, "WAITING")));

        Queue queue = new Queue();
        queue.setRegistrationId(reg.getId());
        queue.setDepartmentId(reg.getDepartmentId());
        queue.setDoctorId(reg.getDoctorId());
        String prefix = reg.getTimePeriod().equals("AM") ? "A" : "P";
        queue.setQueueNo(prefix + String.format("%03d", waitCount + 1));
        queue.setStatus("WAITING");
        queue.setWaitCount(waitCount);
        queueMapper.insert(queue);

        reg.setQueueNo(queue.getQueueNo());
        registrationMapper.updateById(reg);

        if (reg.getHisOrderNo() != null) {
            hisClient.signIn(reg.getHisOrderNo());
        }

        return queue;
    }
}