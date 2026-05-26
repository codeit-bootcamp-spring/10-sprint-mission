package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AdminProperties adminProperties;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(UserRole.ADMIN)) {
      log.info("Admin 계정이 이미 존재합니다. 초기화를 건너뜁니다.");
      return;
    }

    String encodedPassword = passwordEncoder.encode(adminProperties.password());
    User admin = new User(
        adminProperties.username(),
        adminProperties.email(),
        encodedPassword,
        null,
        UserRole.ADMIN
    );
    userRepository.save(admin);

    log.info("Admin 계정 생성 완료: username={}", adminProperties.username());
  }
}
