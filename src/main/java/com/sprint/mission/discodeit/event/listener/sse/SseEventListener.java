package com.sprint.mission.discodeit.event.listener.sse;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.event.*;
import com.sprint.mission.discodeit.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 도메인 이벤트를 감지하여 SSE를 통해 클라이언트에게 실시간 알림 및 갱신 신호를 전송하는 리스너입니다.
 * 각 이벤트별 로직을 명시적으로 분리하여 가독성을 높였습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class SseEventListener {

    private final SseService sseService;
    private final NotificationService notificationService;
    private final BinaryContentService binaryContentService;
    private final ChannelService channelService;
    private final UserService userService;

    /* --- 1. 알림 관련 이벤트 --- */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(NotificationEvents.Created event) {
        log.info("[SSE] 알림 생성 이벤트 수신: ID={}", event.notificationId());

        NotificationDto notification = notificationService.find(event.notificationId());
        sseService.send(List.of(event.receiverId()), "notifications.created", notification);
    }


    /* --- 2. 파일(BinaryContent) 관련 이벤트 --- */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(BinaryContentEvents.Updated event) {
        log.info("[SSE] 파일 상태 변경 이벤트 수신: ID={}", event.binaryContentId());
        
        BinaryContentDto.Response binaryContent = binaryContentService.find(event.binaryContentId());
        sseService.broadcast("binaryContents.updated", binaryContent);
    }

    /* --- 3. 채널 관련 이벤트 --- */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelEvents.Created event) {
        log.info("[SSE] 채널 생성 이벤트 수신: ID={}, Type={}", event.id(), event.type());
        
        ChannelDto.Response channel = channelService.find(event.id());
        if (event.type() == ChannelType.PUBLIC) {
            sseService.broadcast("channels.created", channel);
        } else {
            sseService.send(event.participantIds(), "channels.created", channel);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelEvents.Updated event) {
        log.info("[SSE] 채널 수정 이벤트 수신: ID={}, Type={}", event.id(), event.type());
        
        ChannelDto.Response channel = channelService.find(event.id());
        if (event.type() == ChannelType.PUBLIC) {
            sseService.broadcast("channels.updated", channel);
        } else {
            sseService.send(event.participantIds(), "channels.updated", channel);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ChannelEvents.Deleted event) {
        log.info("[SSE] 채널 삭제 이벤트 수신: ID={}, Type={}", event.id(), event.type());
        
        if (event.type() == ChannelType.PUBLIC) {
            sseService.broadcast("channels.deleted", Map.of("id", event.id()));
        } else {
            sseService.send(event.participantIds(), "channels.deleted", Map.of("id", event.id()));
        }
    }

    /* --- 4. 사용자 관련 이벤트 --- */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserEvents.Created event) {
        log.info("[SSE] 사용자 생성 이벤트 수신: ID={}", event.user().id());

        sseService.broadcast("users.created", event.user());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserEvents.Updated event) {
        log.info("[SSE] 사용자 수정 이벤트 수신: ID={}", event.user().id());

        sseService.broadcast("users.updated", event.user());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserEvents.Deleted event) {
        log.info("[SSE] 사용자 삭제 이벤트 수신: ID={}", event.user().id());
        
        sseService.broadcast("users.deleted", Map.of("id", event.user().id()));
    }

    @EventListener
    public void on(UserEvents.OnlineStatusChanged event) {
        log.info("[SSE] 사용자 온라인 상태 변경 이벤트 수신: ID={}, Online={}", event.userId(), event.online());
        
        UserDto.Response user = userService.find(event.userId());
        sseService.broadcast("users.updated", user);
    }
}
