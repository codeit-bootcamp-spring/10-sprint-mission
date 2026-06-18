package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketRequiredEventListener {

    // 인스턴스 부팅 시 한 번 평가되는 group-id (인스턴스마다 unique)
    private static final String GROUP = "ws-#{T(java.util.UUID).randomUUID()}";

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = GROUP)
    public void handleMessage(String payload) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
            MessageDto message = event.getData();
            UUID channelId = message.channelId();
            String destination = String.format("/sub/channels.%s.messages", channelId);

            log.debug("WebSocket 메시지 발행: destination={}, messageId={}", destination, message.id());
            messagingTemplate.convertAndSend(destination, message);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize MessageCreatedEvent payload", e);
        }
    }
}
