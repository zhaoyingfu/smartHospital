package com.smarthospital.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisPubSubBridge {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Bean
    MessageListenerAdapter queueEventListener() {
        return new MessageListenerAdapter((MessageListener) (message, pattern) -> {
            try {
                String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
                String body = new String(message.getBody(), StandardCharsets.UTF_8);

                @SuppressWarnings("unchecked")
                Map<String, Object> data = objectMapper.readValue(body, Map.class);
                Object deptId = data.get("departmentId");
                Object docId = data.get("doctorId");

                if (deptId != null && docId != null) {
                    String topic = "/topic/queue/" + deptId + "/" + docId;
                    messagingTemplate.convertAndSend(topic, data);
                    log.debug("Redis Pub/Sub bridge → STOMP topic={} event={}", topic, data.get("type"));
                }
            } catch (Exception e) {
                log.warn("Failed to bridge queue event: {}", e.getMessage());
            }
        });
    }

    @Bean
    RedisMessageListenerContainer queueContainer(MessageListenerAdapter queueEventListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(queueEventListener, new ChannelTopic("queue:event:*"));
        return container;
    }
}
