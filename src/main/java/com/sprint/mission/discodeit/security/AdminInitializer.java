package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  @Value("${discodeit.admin.email}")
  private String adminEmail;
  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Override
  public void run(String... args) throws Exception {
    if (!userRepository.existsByRole(Role.ADMIN)) {
      User admin = new User(
          "admin",
          adminEmail,
          passwordEncoder.encode(adminPassword),
          null
      );
      admin.updateRole(Role.ADMIN);
      userRepository.save(admin);
      log.info("[ADMIN_INIT] 초기 어드민 계정 생성 완료");
    } else {
      log.debug("[ADMIN_INIT] 이미 어드민 계정이 존재합니다.");
    }
  }
}
