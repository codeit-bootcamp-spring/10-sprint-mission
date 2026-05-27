package com.sprint.mission.discodeit.auth.service;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import java.util.List;
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
  public void expireUserSession(UUID userId) {
    List<Object> principals = sessionRegistry.getAllPrincipals();

    for (Object principal : principals) {
      if (principal instanceof DiscodeitUserDetails) {
        DiscodeitUserDetails userDetails = (DiscodeitUserDetails) principal;
        if (userDetails.getUserDto().id().equals(userId)) {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
          for (SessionInformation session : sessions) {
            session.expireNow();
          }
        }
      }
    }
  }
}
