package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketFanoutConsumer {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @KafkaListener(topics = KafkaTopics.MESSAGE_CREATED,
            groupId = "${discodeit.kafka.broadcast-group}")
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void onMessage(String payload) throws Exception {
        MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);
        messageRepository.findWithDetailsById(event.messageId())
                .map(messageMapper::toDto)
                .ifPresentOrElse(
                        dto -> messagingTemplate.convertAndSend(
                                "/sub/channels." + event.channelId() + ".messages", dto),
                        () -> log.warn("WS fan-out 대상 메시지를 찾을 수 없음: messageId={}",
                                event.messageId()));
    }
}
