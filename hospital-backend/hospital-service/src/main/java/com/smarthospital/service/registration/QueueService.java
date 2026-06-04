package com.smarthospital.service.registration;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smarthospital.common.exception.BizException;
import com.smarthospital.dal.mapper.PatientMapper;
import com.smarthospital.dal.mapper.QueueMapper;
import com.smarthospital.dal.mapper.RegistrationMapper;
import com.smarthospital.model.entity.Patient;
import com.smarthospital.model.entity.Queue;
import com.smarthospital.model.entity.Registration;
import com.smarthospital.model.vo.queue.QueuePanelVO;
import com.smarthospital.model.vo.queue.QueueVO;
import com.smarthospital.service.queue.QueueEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueMapper queueMapper;
    private final RegistrationMapper registrationMapper;
    private final PatientMapper patientMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final QueueEventPublisher eventPublisher;

    private DefaultRedisScript<Object> zpopminScript;

    @PostConstruct
    void init() {
        zpopminScript = new DefaultRedisScript<>();
        zpopminScript.setLocation(new ClassPathResource("lua/zpopmin_batch.lua"));
        zpopminScript.setResultType(Object.class);
    }

    private String redisKey(Long departmentId, Long doctorId) {
        return "queue:outpatient:" + departmentId + ":" + doctorId;
    }

    @Transactional
    public Queue callNext(Long departmentId, Long doctorId) {
        String key = redisKey(departmentId, doctorId);
        Object raw = redisTemplate.execute(zpopminScript, Collections.singletonList(key), "1");
        if (raw == null) throw new BizException("队列已空");

        Long queueId = Long.valueOf(raw.toString().replace("queue:", ""));
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) throw new BizException("排队记录不存在");

        queue.setStatus("CALLING");
        queue.setCallTime(LocalDateTime.now());
        queue.setCallCount(queue.getCallCount() == null ? 1 : queue.getCallCount() + 1);
        queueMapper.updateById(queue);

        eventPublisher.publish(queue, "CALL_NEXT");
        return queue;
    }

    @Transactional
    public Queue recall(Long queueId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) throw new BizException("排队记录不存在");
        queue.setStatus("CALLING");
        queue.setCallTime(LocalDateTime.now());
        queue.setCallCount(queue.getCallCount() == null ? 0 : queue.getCallCount() + 1);
        queueMapper.updateById(queue);
        eventPublisher.publish(queue, "RECALL");
        return queue;
    }

    @Transactional
    public Queue skip(Long queueId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) throw new BizException("排队记录不存在");

        queue.setStatus("SKIPPED");
        queueMapper.updateById(queue);
        eventPublisher.publish(queue, "SKIP");
        return queue;
    }

    @Transactional
    public Queue startConsultation(Long queueId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) throw new BizException("排队记录不存在");
        queue.setStatus("SEEING");
        queueMapper.updateById(queue);
        eventPublisher.publish(queue, "START_DIAG");
        return queue;
    }

    @Transactional
    public Queue complete(Long queueId) {
        Queue queue = queueMapper.selectById(queueId);
        if (queue == null) throw new BizException("排队记录不存在");
        queue.setStatus("DONE");
        queueMapper.updateById(queue);

        String member = "queue:" + queueId;
        redisTemplate.opsForZSet().remove(redisKey(queue.getDepartmentId(), queue.getDoctorId()), member);

        Registration reg = registrationMapper.selectById(queue.getRegistrationId());
        if (reg != null) {
            reg.setStatus("SEEN");
            registrationMapper.updateById(reg);
        }

        eventPublisher.publish(queue, "COMPLETE");
        return queue;
    }

    public QueuePanelVO getPanelData(Long departmentId, Long doctorId) {
        String key = redisKey(departmentId, doctorId);
        Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
        if (members == null) members = Collections.emptySet();

        QueueVO current = getCurrentCallingVO(departmentId, doctorId);
        List<QueueVO> waitingList = new ArrayList<>();
        int rank = 0;

        Map<Long, String> nameCache = new HashMap<>();

        for (Object member : members) {
            Long qid = Long.valueOf(member.toString().replace("queue:", ""));
            Queue q = queueMapper.selectById(qid);
            if (q == null) continue;
            QueueVO vo = toVO(q, nameCache);
            vo.setRank(rank++);
            waitingList.add(vo);
        }

        QueuePanelVO panel = new QueuePanelVO();
        panel.setCurrentCalling(current);
        panel.setWaitingList(waitingList);
        panel.setTotalWaiting((long) waitingList.size());
        panel.setDepartmentId(departmentId);
        panel.setDoctorId(doctorId);
        return panel;
    }

    public List<Queue> queryByDepartmentAndDoctor(Long departmentId, Long doctorId) {
        return queueMapper.selectList(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDepartmentId, departmentId)
                        .eq(Queue::getDoctorId, doctorId)
                        .orderByAsc(Queue::getCreateTime));
    }

    public Long getWaitCount(Long departmentId, Long doctorId) {
        String key = redisKey(departmentId, doctorId);
        Long size = redisTemplate.opsForZSet().size(key);
        return size != null ? size : 0L;
    }

    private QueueVO getCurrentCallingVO(Long departmentId, Long doctorId) {
        Queue q = queueMapper.selectOne(
                new LambdaQueryWrapper<Queue>()
                        .eq(Queue::getDepartmentId, departmentId)
                        .eq(Queue::getDoctorId, doctorId)
                        .eq(Queue::getStatus, "CALLING")
                        .orderByDesc(Queue::getCallTime)
                        .last("LIMIT 1"));
        return q != null ? toVO(q, new HashMap<>()) : null;
    }

    private QueueVO toVO(Queue q, Map<Long, String> nameCache) {
        QueueVO vo = new QueueVO();
        vo.setId(q.getId());
        vo.setQueueNo(q.getQueueNo());
        vo.setStatus(q.getStatus());
        vo.setCallCount(q.getCallCount());
        vo.setType(q.getType());
        vo.setWaitCount(q.getWaitCount());
        vo.setCallTime(q.getCallTime());
        vo.setDepartmentId(q.getDepartmentId());
        vo.setDoctorId(q.getDoctorId());

        Registration reg = registrationMapper.selectById(q.getRegistrationId());
        if (reg != null) {
            vo.setRegNo(reg.getRegNo());
            Long pid = reg.getPatientId();
            if (pid != null) {
                String name = nameCache.computeIfAbsent(pid, id -> {
                    Patient p = patientMapper.selectById(id);
                    return p != null ? p.getName() : null;
                });
                vo.setPatientName(name);
            }
        }
        return vo;
    }
}
