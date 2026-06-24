package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.event.ChannelChangeEvent;
import com.sprint.mission.discodeit.event.PrivateChannelChangeEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// ChannelChangeEvent를 받아 이벤트를 전송
@Component
@Slf4j
@RequiredArgsConstructor
public class ChannelSseEventListener {

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async(value = "eventTaskExecutor")
    public void on(ChannelChangeEvent event) {
        String changeType = switch (event.getChangeType()) {
            case CREATED -> "channels.created";
            case UPDATED -> "channels.updated";
            case DELETED -> "channels.deleted";
        };
        ChannelDto channelDto = event.getChannelDto();

        sseService.broadcast(changeType, channelDto);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async(value = "eventTaskExecutor")
    public void on(PrivateChannelChangeEvent event) {
        String changeType = switch (event.getChangeType()) {
            case CREATED -> "channels.created";
            case UPDATED -> "channels.updated";
            case DELETED -> "channels.deleted";
        };
        ChannelDto channelDto = event.getChannelDto();
        Set<UUID> participationIds = channelDto.participants().stream()
                .map(participant -> participant.id())
                .collect(Collectors.toSet());

        sseService.send(participationIds, changeType, channelDto);
    }
}
