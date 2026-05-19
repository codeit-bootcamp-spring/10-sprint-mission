package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {

  private final SessionRegistry sessionRegistry;

  @Override
  public void expireUserSessions(UUID userId) {
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      if (principal instanceof DiscodeitUserDetails userDetails) {
        if (userDetails.getUserDto().id().equals(userId)) {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
          for (SessionInformation session : sessions) {
            session.expireNow();
            log.debug("권한 변경 유저의 세션 만료 처리 완료: sessionId={}", session.getSessionId());
          }
        }
      }
    }
  }

  @Override
  public boolean isUserLoggedIn(UUID userId) {
    for (Object principal : sessionRegistry.getAllPrincipals()) {
      if (principal instanceof DiscodeitUserDetails userDetails) {
        if (userDetails.getUserDto().id().equals(userId)) {
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
          return !sessions.isEmpty();
        }
      }
    }
    return false;
  }
}
