package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.auth.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final SessionRegistry sessionRegistry;

  @Override
  public void expireUserSessions(UUID userId) {
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      if (principal instanceof DiscodeitUserDetails userDetails) {
        if (userDetails.getUserDto().getId().equals(userId)) {
          sessionRegistry.getAllSessions(principal, false)
              .forEach(SessionInformation::expireNow);
        }
      }
    }
  }

  @Override
  public boolean isUserOnline(UUID userId) {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> (DiscodeitUserDetails) principal)
        .anyMatch(userDetails -> userDetails.getUserDto().getId().equals(userId));
  }
}
