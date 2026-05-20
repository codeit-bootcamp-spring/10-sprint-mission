package com.sprint.mission.discodeit.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserOnlineStatusChecker {

    private final SessionRegistry sessionRegistry;

    public boolean isOnline(UUID userId) {
        System.out.println("principals = " + sessionRegistry.getAllPrincipals());

        return sessionRegistry.getAllPrincipals().stream()
                .filter(DiscodeitUserDetails.class::isInstance)
                .map(DiscodeitUserDetails.class::cast)
                .peek(u -> System.out.println("principal userId = " + u.getUserDto().id()))
                .filter(userDetails -> userDetails.getUserDto().id().equals(userId))
                .anyMatch(userDetails ->
                        !sessionRegistry.getAllSessions(userDetails, false).isEmpty()
                );
    }
}
