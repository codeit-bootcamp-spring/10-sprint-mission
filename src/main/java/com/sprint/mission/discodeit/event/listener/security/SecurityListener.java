package com.sprint.mission.discodeit.event.listener.security;

import com.sprint.mission.discodeit.event.UserEvents;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 보안 관련 이벤트를 구독하여 세션 관리 등을 수행하는 리스너입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityListener {

    private final AuthService authService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRoleUpdated(UserEvents.RoleUpdated event) {
        log.info("[SecurityListener] 권한 변경으로 인한 세션 만료 처리: UserId={}", event.userId());
        authService.expireUserSessions(event.userId());
    }
}
