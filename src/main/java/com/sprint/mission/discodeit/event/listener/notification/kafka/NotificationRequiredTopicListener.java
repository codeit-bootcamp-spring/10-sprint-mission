package com.sprint.mission.discodeit.event.listener.notification.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageEvents;
import com.sprint.mission.discodeit.event.SystemEvents;
import com.sprint.mission.discodeit.event.UserEvents;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("prod")
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final NotificationEventService notificationEventService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        MessageEvents.Created event = fromJson(kafkaEvent, MessageEvents.Created.class);
        notificationEventService.sendByMessageCreated(event.messageId());
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        UserEvents.RoleUpdated event = fromJson(kafkaEvent, UserEvents.RoleUpdated.class);
        notificationEventService.sendByRoleUpdated(event.userId(), event.oldRole(), event.newRole());
    }

    @KafkaListener(topics = "discodeit.AsyncErrorAlarmEvent")
    public void onAsyncErrorAlarmEvent(String kafkaEvent) {
        SystemEvents.AsyncErrorAlarm event = fromJson(kafkaEvent, SystemEvents.AsyncErrorAlarm.class);
        notificationEventService.sendAsyncErrorNotification(event.requestId(), event.methodName(), event.errorMessage());
    }

    private <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 역직렬화 실패: JSON={}, Type={}, Error={}", json, clazz.getSimpleName(), e.getMessage(), e);
            throw InternalServerException.dataIntegrity("Kafka 메시지 역직렬화 중 오류가 발생했습니다.");
        }
    }
}

