package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public User login(String username, String password) {
    log.info("Login attempt for username: {}", username); // 로그인 시도 로그

    // 유저 확인
    User user = userRepository.findByUsernameWithProfileAndStatus(username)
        .orElseThrow(UserNotFoundException::new);

    // 비밀번호 확인
    if (!user.getPassword().equals(password)) {
      throw new InvalidPasswordException();
    }

    // 유저 상태 조회 및 업데이트
    UserStatus status = user.getUserStatus();
    if (status == null) {
      throw new UserStatusNotFoundException();
    }

    status.updateLastActiveAt(Instant.now());

    log.info("User {} logged in successfully", username); // 로그인 성공 로그
    return user;
  }
}
