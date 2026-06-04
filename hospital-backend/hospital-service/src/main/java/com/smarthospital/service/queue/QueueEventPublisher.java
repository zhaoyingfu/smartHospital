package com.smarthospital.service.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarthospital.model.entity.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public void publish(Queue queue, String eventType) {
        java.util.Map<String, Object> msg = new java.util.HashMap<>();
        msg.put("type", eventType);
        msg.put("queueId", queue.getId());
        msg.put("queueNo", queue.getQueueNo());
        msg.put("status", queue.getStatus());
        msg.put("departmentId", queue.getDepartmentId());
        msg.put("doctorId", queue.getDoctorId());
        msg.put("timestamp", System.currentTimeMillis());
        try {
            redisTemplate.convertAndSend("queue:event:" + queue.getDepartmentId() + ":" + queue.getDoctorId(), MAPPER.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize queue event", e);
        }
    }
}
