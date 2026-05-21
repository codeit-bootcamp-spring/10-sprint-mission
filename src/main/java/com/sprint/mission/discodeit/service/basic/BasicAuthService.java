package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Transactional
  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.debug("사용자 역할 변경 시작: userId={}, newRole={}", request.userId(), request.role());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

    user.updateRole(request.role());
    userRepository.save(user);

    sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof com.sprint.mission.discodeit.config.DiscodeitUserDetails)
        .filter(principal -> ((com.sprint.mission.discodeit.config.DiscodeitUserDetails) principal)
            .getUserDto().id().equals(request.userId()))
        .forEach(principal -> {
          sessionRegistry.getAllSessions(principal, false)
              .forEach(session -> session.expireNow());
        });

    log.info("사용자 역할 변경 완료: userId={}, newRole={}", request.userId(), request.role());
    return userMapper.toDto(user);
  }
}
