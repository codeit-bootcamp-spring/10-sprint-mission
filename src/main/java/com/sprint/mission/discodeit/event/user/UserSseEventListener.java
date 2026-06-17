package com.sprint.mission.discodeit.event.user;

import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class UserSseEventListener {
    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserUpdateEvent event) {
        sseService.broadcast("users.updated", event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserCreateEvent event) {
        sseService.broadcast("users.created", event.getUser());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(UserDeleteEvent event) {
        sseService.broadcast("users.deleted", event.getUser());
    }
}
