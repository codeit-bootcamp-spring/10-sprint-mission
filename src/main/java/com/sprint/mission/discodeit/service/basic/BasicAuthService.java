package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;

import com.sprint.mission.discodeit.util.UserSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final UserSessionManager userSessionManager;

  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.debug("사용자 권한 변경: userId={}, newRole={}", request.userId(), request.newRole());
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", request.userId()));

    if (user.getRole() == request.newRole()) {
      log.info("사용자 권한 변경 요청(동일한 권한, 세션 만료x): userId={}", request.userId());
      return userMapper.toDto(user, userSessionManager.isOnline(user));
    }
    user.updateRole(request.newRole());
    userSessionManager.setOffline(user);
    log.info("사용자 권한 변경 및 세션 만료 완료: userId={}, newRole={}", request.userId(), request.newRole());
    return userMapper.toDto(user, false);
  }

}
