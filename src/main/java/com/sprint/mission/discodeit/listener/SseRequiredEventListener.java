package com.sprint.mission.discodeit.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelChangedEvent;
import com.sprint.mission.discodeit.event.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.UserChangedEvent;
import com.sprint.mission.discodeit.event.kafka.KafkaTopics;
import com.sprint.mission.discodeit.event.kafka.SseFanoutMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NotificationCreatedEvent event) {
        publishFanout(new SseFanoutMessage("notifications.created",
                event.dto(), List.of(event.dto().receiverId())));
    }

    private void publishFanout(SseFanoutMessage message) {
        try {
            kafkaTemplate.send(KafkaTopics.SSE_FANOUT, objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            log.error("SSE fan-out 발행 실패: eventName={}", message.eventName(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(BinaryContentStatusUpdatedEvent event) {
        publishFanout(new SseFanoutMessage("binaryContents.updated", event.dto(), null));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelChangedEvent event) {
        ChannelDto channel = event.dto();
        String eventName = "channels." + event.action();
        if (channel.type() == ChannelType.PUBLIC) {
            publishFanout(new SseFanoutMessage(eventName, channel, null));
        } else {
            publishFanout(new SseFanoutMessage(eventName, channel,
                    channel.participants().stream().map(UserDto::id).toList()));
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserChangedEvent event) {
        publishFanout(new SseFanoutMessage("users." + event.action(), event.dto(), null));
    }
}
