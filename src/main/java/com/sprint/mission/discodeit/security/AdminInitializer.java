package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("!prod") // 운영 환경이 아닐 때만 admin 시드 동작
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  // yaml 파일에서 어드민 계정 정보 주입
  @Value("${discodeit.admin.username}")
  private String adminUsername;

  @Value("${discodeit.admin.email}")
  private String adminEmail;

  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    // 어드민 계정이 없을 때만 생성
    if (!userRepository.existsByRole(Role.ADMIN)) {
      log.info("ADMIN account not found. Proceeding with initialization.");
      User admin = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword),
          null);
      admin.updateRole(Role.ADMIN);

      userRepository.save(admin);
      log.info("ADMIN account created successfully.");
    } else {
      log.info("ADMIN account already exists. Skipping initialization.");
    }
  }
}
