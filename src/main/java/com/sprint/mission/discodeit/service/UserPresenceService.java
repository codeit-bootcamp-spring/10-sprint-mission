package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPresenceService {

  private final SessionRegistry sessionRegistry;

  public boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(principal -> principal.getUserDto().id().equals(userId))
        .anyMatch(principal -> sessionRegistry.getAllSessions(principal, false).stream()
            .anyMatch(session -> !session.isExpired()));
  }
}
