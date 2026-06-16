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

    // 로그인 로그아웃은 트랜잭션이 없으므로 fallbackExecution로 트랜잭션없을때도 작동하도록함
    @Async("ioTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleUserUpdated(UserUpdatedEvent event){
        for(SseDto dto : event.getDtos()){
            // 로그인 로그아웃시 모든 유저가 알 수 있게 브로드캐스팅
            if(dto.getReceiverId() == null){
                log.info("sse 브로드캐스트 완료 ");
                sseService.broadcast(dto.getEventName(), dto.getDto());
                continue;
            }
            log.info("sse발송 완료 receiverId = {}", dto.getReceiverId());
            sseService.send(List.of(dto.getReceiverId()), dto.getEventName(), dto.getDto());
        }
    }
}
