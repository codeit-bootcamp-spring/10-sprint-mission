package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.channel.ChannelCreatedEvent;
import com.sprint.mission.discodeit.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.channel.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.notification.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.user.UserCreatedEvent;
import com.sprint.mission.discodeit.event.user.UserDeletedEvent;
import com.sprint.mission.discodeit.event.user.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SseRequiredEventListener {

    private static final String NOTIFICATIONS_CREATED = "notifications.created";
    private static final String BINARY_CONTENTS_UPDATED = "binaryContents.updated";
    private static final String CHANNELS_CREATED = "channels.created";
    private static final String CHANNELS_UPDATED = "channels.updated";
    private static final String CHANNELS_DELETED = "channels.deleted";
    private static final String USERS_CREATED = "users.created";
    private static final String USERS_UPDATED = "users.updated";
    private static final String USERS_DELETED = "users.deleted";

    // 인스턴스 부팅 시 한 번 평가되는 group-id (인스턴스마다 unique)
    private static final String GROUP = "sse-#{T(java.util.UUID).randomUUID()}";

    private final SseService sseService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.NotificationCreatedEvent", groupId = GROUP)
    public void onNotificationCreated(String payload) {
        NotificationCreatedEvent event = readValue(payload, NotificationCreatedEvent.class);
        if (event == null) {
            return;
        }
        sseService.send(Set.of(event.getReceiverId()), NOTIFICATIONS_CREATED, event.getNotification());
    }

    @KafkaListener(topics = "discodeit.BinaryContentUpdatedEvent", groupId = GROUP)
    public void onBinaryContentUpdated(String payload) {
        BinaryContentUpdatedEvent event = readValue(payload, BinaryContentUpdatedEvent.class);
        if (event == null) {
            return;
        }
        sseService.broadcast(BINARY_CONTENTS_UPDATED, event.getBinaryContent());
    }

    @KafkaListener(topics = "discodeit.ChannelCreatedEvent", groupId = GROUP)
    public void onChannelCreated(String payload) {
        ChannelCreatedEvent event = readValue(payload, ChannelCreatedEvent.class);
        if (event == null) {
            return;
        }
        dispatchChannel(CHANNELS_CREATED, event.getChannel(), event.getReceiverIds());
    }

    @KafkaListener(topics = "discodeit.ChannelUpdatedEvent", groupId = GROUP)
    public void onChannelUpdated(String payload) {
        ChannelUpdatedEvent event = readValue(payload, ChannelUpdatedEvent.class);
        if (event == null) {
            return;
        }
        dispatchChannel(CHANNELS_UPDATED, event.getChannel(), event.getReceiverIds());
    }

    @KafkaListener(topics = "discodeit.ChannelDeletedEvent", groupId = GROUP)
    public void onChannelDeleted(String payload) {
        ChannelDeletedEvent event = readValue(payload, ChannelDeletedEvent.class);
        if (event == null) {
            return;
        }
        dispatchChannel(CHANNELS_DELETED, event.getChannel(), event.getReceiverIds());
    }

    @KafkaListener(topics = "discodeit.UserCreatedEvent", groupId = GROUP)
    public void onUserCreated(String payload) {
        UserCreatedEvent event = readValue(payload, UserCreatedEvent.class);
        if (event == null) {
            return;
        }
        sseService.broadcast(USERS_CREATED, event.getUser());
    }

    @KafkaListener(topics = "discodeit.UserUpdatedEvent", groupId = GROUP)
    public void onUserUpdated(String payload) {
        UserUpdatedEvent event = readValue(payload, UserUpdatedEvent.class);
        if (event == null) {
            return;
        }
        sseService.broadcast(USERS_UPDATED, event.getUser());
    }

    @KafkaListener(topics = "discodeit.UserDeletedEvent", groupId = GROUP)
    public void onUserDeleted(String payload) {
        UserDeletedEvent event = readValue(payload, UserDeletedEvent.class);
        if (event == null) {
            return;
        }
        sseService.broadcast(USERS_DELETED, event.getUser());
    }

    private void dispatchChannel(String eventName, Object data, Set<UUID> receiverIds) {
        if (receiverIds == null || receiverIds.isEmpty()) {
            sseService.broadcast(eventName, data);
        } else {
            sseService.send(receiverIds, eventName, data);
        }
    }

    private <T> T readValue(String payload, Class<T> type) {
        try {
            return objectMapper.readValue(payload, type);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize Kafka payload: type={}, payload={}", type.getSimpleName(), payload, e);
            return null;
        }
    }
}
