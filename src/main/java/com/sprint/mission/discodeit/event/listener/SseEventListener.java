package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.dto.sse.SseDto;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.event.NotificationsCreatedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseEventListener {
    private final SseService sseService;

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationsCreated(NotificationsCreatedEvent event) {

        for (NotificationDto dto : event.getDtos()) {
            sseService.send(List.of(dto.getReceiverId()), "notifications.created", dto);
        }
    }

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentUpdated(BinaryContentUpdatedEvent event){
        for(SseDto dto : event.getDtos()){
            log.info("sse발송 완료 receiverId = {}", dto.getReceiverId());
            sseService.send(List.of(dto.getReceiverId()), dto.getEventName(), dto.getDto());
        }
    }

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChannelUpdated(ChannelUpdatedEvent event){
        for(SseDto dto : event.getDtos()){
            log.info("sse발송 완료 receiverId = {}", dto.getReceiverId());
            sseService.send(List.of(dto.getReceiverId()), dto.getEventName(), dto.getDto());
        }
    }

    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserUpdated(UserUpdatedEvent event){
        for(SseDto dto : event.getDtos()){
            log.info("sse발송 완료 receiverId = {}", dto.getReceiverId());
            sseService.send(List.of(dto.getReceiverId()), dto.getEventName(), dto.getDto());
        }
    }
}
