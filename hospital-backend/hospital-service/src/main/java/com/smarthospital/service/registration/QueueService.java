package com.smarthospital.service.registration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.dal.mapper.QueueMapper;
import com.smarthospital.dal.mapper.RegistrationMapper;
import com.smarthospital.model.entity.Queue;
import com.smarthospital.model.entity.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueMapper queueMapper;
    private final RegistrationMapper registrationMapper;

    public List<Queue> queryByDepartmentAndDoctor(Long departmentId, Long doctorId) {
        return queueMapper.selectList(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDepartmentId, departmentId)
                        .eq(Queue::getDoctorId, doctorId)
                        .eq(Queue::getStatus, "WAITING")
                        .orderByAsc(Queue::getCreateTime));
    }

    public Long getWaitCount(Long departmentId, Long doctorId) {
        return  queueMapper.selectCount(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDepartmentId, departmentId)
                        .eq(Queue::getDoctorId, doctorId)
                        .eq(Queue::getStatus, "WAITING"));
    }

    public Queue getCurrentServing(Long departmentId, Long doctorId) {
        return queueMapper.selectOne(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDepartmentId, departmentId)
                        .eq(Queue::getDoctorId, doctorId)
                        .eq(Queue::getStatus, "SEEING")
                        .orderByDesc(Queue::getCallTime)
                        .last("LIMIT 1"));
    }
}