package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.authdto.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(
      RoleUpdateRequest req
  ) {
    Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

    User user = userRepository.findById(req.userId())
        .orElseThrow(() -> new UserNotFoundException(req.userId()));

    user.updateRole(req.newRole());
    expireUserSessions(user);

    return userMapper.toDto(user);
  }

  private void expireUserSessions(User user) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(principal -> Objects.equals(principal.getUserDto().id(), user.getId()))
        .forEach(principal -> sessionRegistry.getAllSessions(principal, false)
            .forEach(sessionInformation -> sessionInformation.expireNow()));
  }

}
