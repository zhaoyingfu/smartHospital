package com.smarthospital.service.registration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.dal.mapper.QueueMapper;
import com.smarthospital.dal.mapper.RegistrationMapper;
import com.smarthospital.model.entity.Queue;
import com.smarthospital.model.entity.Registration;
import com.smarthospital.service.queue.QueueEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignInService {

    private static final long N = 3;
    private static final long STEP_MS = 600_000;
    private static final long EMERGENCY_OFFSET_MS = 86_400_000;

    private final RegistrationMapper registrationMapper;
    private final QueueMapper queueMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QueueEventPublisher eventPublisher;

    private DefaultRedisScript<List> enqueueScript;
    private DefaultRedisScript<Long> reenterSkipScript;

    @SuppressWarnings("unchecked")
    @PostConstruct
    void init() {
        enqueueScript = new DefaultRedisScript<>();
        enqueueScript.setLocation(new ClassPathResource("lua/enqueue.lua"));
        enqueueScript.setResultType(List.class);

        reenterSkipScript = new DefaultRedisScript<>();
        reenterSkipScript.setLocation(new ClassPathResource("lua/reenter_skip.lua"));
        reenterSkipScript.setResultType(Long.class);
    }

    private String redisKey(Long departmentId, Long doctorId) {
        return "queue:outpatient:" + departmentId + ":" + doctorId;
    }

    @Transactional
    public Queue signIn(Long patientId, String regNo) {
        Registration reg = registrationMapper.selectOne(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getRegNo, regNo)
                        .eq(Registration::getPatientId, patientId));
        if (reg == null) throw new BizException("挂号记录不存在");
        if (!"REGISTERED".equals(reg.getStatus()) && !"SIGNED_IN".equals(reg.getStatus())) {
            throw new BizException("当前挂号状态不允许签到");
        }

        String patientType = resolvePatientType(patientId, reg.getDoctorId(), reg.getRegDate());

        reg.setStatus("SIGNED_IN");
        reg.setSignTime(LocalDateTime.now());
        registrationMapper.updateById(reg);

        Long existingWaiting = queueMapper.selectCount(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDoctorId, reg.getDoctorId())
                        .eq(Queue::getDepartmentId, reg.getDepartmentId())
                        .eq(Queue::getStatus, "WAITING"));

        String prefix = reg.getRegDate().equals(LocalDate.now()) ?
                (reg.getTimePeriod().equals("AM") ? "A" : "P") : "Y";
        String queueNo = prefix + String.format("%03d", existingWaiting + 1);

        Queue queue = new Queue();
        queue.setRegistrationId(reg.getId());
        queue.setDepartmentId(reg.getDepartmentId());
        queue.setDoctorId(reg.getDoctorId());
        queue.setQueueNo(queueNo);
        queue.setStatus("WAITING");
        queue.setWaitCount((int) (long) existingWaiting);
        queue.setType(patientType);
        queue.setCallCount(0);
        queueMapper.insert(queue);

        reg.setQueueNo(queueNo);
        registrationMapper.updateById(reg);

        String key = redisKey(reg.getDepartmentId(), reg.getDoctorId());
        long signTs = System.currentTimeMillis();
        redisTemplate.execute(enqueueScript,
                Collections.singletonList(key),
                String.valueOf(queue.getId()),
                patientType,
                String.valueOf(signTs),
                String.valueOf(N),
                String.valueOf(STEP_MS),
                String.valueOf(EMERGENCY_OFFSET_MS));

        eventPublisher.publish(queue, "SIGN_IN");

        Long rank = redisTemplate.opsForZSet().rank(key, "queue:" + queue.getId());
        if (rank != null) queue.setWaitCount(rank.intValue());

        return queue;
    }

    @Transactional
    public Queue reenterSkip(Long patientId, String regNo) {
        Registration reg = registrationMapper.selectOne(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getRegNo, regNo)
                        .eq(Registration::getPatientId, patientId));
        if (reg == null) throw new BizException("挂号记录不存在");

        Queue skipped = queueMapper.selectOne(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getRegistrationId, reg.getId())
                        .eq(Queue::getStatus, "SKIPPED")
                        .orderByDesc(Queue::getCreateTime)
                        .last("LIMIT 1"));
        if (skipped == null) throw new BizException("没有过号记录");

        if (skipped.getCallCount() != null && skipped.getCallCount() >= 2) {
            throw new BizException("已过号两次，请到导医台处理");
        }

        skipped.setStatus("WAITING");
        skipped.setCallCount(0);
        queueMapper.updateById(skipped);

        String key = redisKey(reg.getDepartmentId(), reg.getDoctorId());
        long signTs = System.currentTimeMillis();
        long halfN = N / 2;
        redisTemplate.execute(reenterSkipScript,
                Collections.singletonList(key),
                String.valueOf(skipped.getId()),
                String.valueOf(halfN),
                String.valueOf(signTs),
                String.valueOf(STEP_MS));

        Long rank = redisTemplate.opsForZSet().rank(key, "queue:" + skipped.getId());
        if (rank != null) skipped.setWaitCount(rank.intValue());

        eventPublisher.publish(skipped, "REENTER");
        return skipped;
    }

    private String resolvePatientType(Long patientId, Long doctorId, LocalDate regDate) {
        List<Registration> todayDone = registrationMapper.selectList(
                new LambdaQueryWrapper<Registration>()
                        .eq(Registration::getPatientId, patientId)
                        .eq(Registration::getDoctorId, doctorId)
                        .eq(Registration::getRegDate, regDate)
                        .eq(Registration::getStatus, "SEEN")
                        .last("LIMIT 1"));
        return todayDone.isEmpty() ? "FIRST_VISIT" : "REVISIT";
    }
}
