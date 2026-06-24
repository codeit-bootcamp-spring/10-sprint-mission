package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseFanoutConsumer {

    private final ObjectMapper objectMapper;
    private final SseService sseService;

    @KafkaListener(topics = KafkaTopics.SSE_FANOUT,
            groupId = "${discodeit.kafka.broadcast-group}")
    public void onFanout(String payload) throws Exception {
        SseFanoutMessage message = objectMapper.readValue(payload, SseFanoutMessage.class);
        if (message.receiverIds() == null) {
            sseService.broadcast(message.eventName(), message.data());
        } else {
            sseService.send(message.receiverIds(), message.eventName(), message.data());
        }
    }
}
