package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSessionRegistry extends SessionRegistryImpl {

  private final Map<String, SessionInformation> sessions =
      new ConcurrentHashMap<>();

  private final Map<UUID, Set<String>> userSessions =
      new ConcurrentHashMap<>();

  @Override
  public void registerNewSession(String sessionId, Object principal) {

    super.registerNewSession(sessionId, principal);
    DiscodeitUserDetails user =
        (DiscodeitUserDetails) principal;

    UUID userId = user.getId();

    sessions.put(
        sessionId,
        new SessionInformation(principal, sessionId, new Date())
    );

    userSessions
        .computeIfAbsent(
            userId,
            key -> ConcurrentHashMap.newKeySet()
        )
        .add(sessionId);
  }

  @Override
  public void removeSessionInformation(String sessionId) {

    super.removeSessionInformation(sessionId);

    SessionInformation information =
        sessions.remove(sessionId);

    if (information == null) {
      return;
    }

    DiscodeitUserDetails user =
        (DiscodeitUserDetails) information.getPrincipal();

    UUID userId = user.getId();

    Set<String> sessionIds = userSessions.get(userId);

    if (sessionIds != null) {
      sessionIds.remove(sessionId);

      if (sessionIds.isEmpty()) {
        userSessions.remove(userId);
      }
    }
  }

  public boolean isOnline(UUID userId) {

    Set<String> sessionIds =
        userSessions.getOrDefault(userId, Set.of());

    return sessionIds.stream()
        .map(sessions::get)
        .filter(Objects::nonNull)
        .anyMatch(session -> !session.isExpired());
  }
}