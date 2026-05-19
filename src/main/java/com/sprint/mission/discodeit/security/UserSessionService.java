package com.sprint.mission.discodeit.security;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionService {
  private final SessionRegistry sessionRegistry;

  @Named("isOnline")
  public Boolean isOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(principal -> principal.getUserDto().id().equals(userId))
        .anyMatch(principal -> !sessionRegistry.getAllSessions(principal, false).isEmpty());
  }
}
