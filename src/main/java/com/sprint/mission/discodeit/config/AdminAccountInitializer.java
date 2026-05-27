package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminAccountInitializer implements ApplicationRunner {
  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_EMAIL = "admin@discodeit.com";
  private static final String ADMIN_PASSWORD = "Admin123!";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (userRepository.existsByUsername(ADMIN_USERNAME)) {
      log.info("어드민 계정이 이미 존재합니다. 초기화를 건너뜁니다.");
      return;
    }

    User admin = new User(
        ADMIN_USERNAME,
        ADMIN_EMAIL,
        passwordEncoder.encode(ADMIN_PASSWORD),
        Role.ADMIN,
        null
    );

    userRepository.save(admin);

    log.info("어드민 계정이 초기화되었습니다. username={}", ADMIN_USERNAME);
  }
}
