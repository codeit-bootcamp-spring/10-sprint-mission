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
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    // 어드민 계정이 없을 때만 생성
    if (!userRepository.existsByRole(Role.ADMIN)) {
      log.info("ADMIN account not found. Proceeding with initialization.");
      User admin = new User("admin", "admin@admin.com", passwordEncoder.encode("admin1234!"), null);
      admin.updateRole(Role.ADMIN);

      userRepository.save(admin);
      log.info("ADMIN account created successfully.");
    } else {
      log.info("ADMIN account already exists. Skipping initialization.");
    }
  }
}
