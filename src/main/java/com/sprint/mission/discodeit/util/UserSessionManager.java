package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSessionManager {

  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  // 단건 조회
  public boolean isOnline(User user) {
    UserDetails userDetails = new DiscodeitUserDetails(userMapper.toDto(user, true),
        user.getPassword());
    List<SessionInformation> sessionList = sessionRegistry.getAllSessions(userDetails, false);
    return !sessionList.isEmpty();
  }

  // online user 목록
  public Set<UUID> getOnlineUserIds() {
    return sessionRegistry.getAllPrincipals().stream()
        .map(p -> (DiscodeitUserDetails) p)
        .filter(d -> !sessionRegistry.getAllSessions(d, false).isEmpty())
        .map(d -> d.getUserDto().id())
        .collect(Collectors.toSet());
  }

  // 유저 세션 만료
  public void setOffline(User user) {
    UserDetails userDetails = new DiscodeitUserDetails(userMapper.toDto(user, true),
        user.getPassword());
    List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(userDetails,
        false);
    sessionInformations.forEach(SessionInformation::expireNow);
  }
}
