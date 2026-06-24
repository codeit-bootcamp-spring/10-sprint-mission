package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.UserChangeEvent;
import com.sprint.mission.discodeit.event.UserOnlineStatusUpdateEvent;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// UserChangeEvent를 받아 이벤트를 전송
@Component
@Slf4j
@RequiredArgsConstructor
public class UserSseEventListener {

    private final UserService userService;
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async(value = "eventTaskExecutor")
    public void on(UserChangeEvent event) {
        String changeType = switch (event.getChangeType()) {
            case CREATED -> "users.created";
            case UPDATED -> "users.updated";
            case DELETED -> "users.deleted";
        };
        UserDto userDto = event.getUserDto();

        sseService.broadcast(changeType, userDto);
    }

    @EventListener
    @Async(value = "eventTaskExecutor")
    public void on(UserOnlineStatusUpdateEvent event) {
        String eventName = "users.updated";
        UserDto userDto = event.getUserDto() != null
                ? event.getUserDto()
                : userService.find(event.getUserId());

        sseService.broadcast(eventName, userDto);
    }
}
