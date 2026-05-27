package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserOnlineStatusProvider {

    private final SessionRegistry sessionRegistry;

    public boolean isOnline(UUID userId) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            if (!(principal instanceof DiscodeitUserDetails userDetails)) {
                continue;
            }

            if (!userDetails.getUserDto().id().equals(userId)) {
                continue;
            }

            for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                if (!session.isExpired()) {
                    return true;
                }
            }
        }

        return false;
    }
}
