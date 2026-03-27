package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
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
        .orElseThrow(() -> {
          log.warn("Login failed: Username {} not found", username); // 아이디 없음 로그
          return new NoSuchElementException("일치하는 유저가 없습니다.");
        });

    // 비밀번호 확인
    if (!user.getPassword().equals(password)) {
      log.warn("Login failed: Incorrect password for username {}", username); // 비밀번호 틀림 로그
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    // 유저 상태 조회 및 업데이트
    UserStatus status = user.getUserStatus();
    if (status == null) {
      log.error("Data integrity issue: UserStatus missing for user ID: {}",
          user.getId()); // 있어야 할 상태 정보가 없어 ERROR 로그
      throw new NoSuchElementException("유저 상태 정보가 존재하지 않습니다.");
    }

    status.updateLastActiveAt(Instant.now());

    log.info("User {} logged in successfully", username); // 로그인 성공 로그
    return user;
  }
}
