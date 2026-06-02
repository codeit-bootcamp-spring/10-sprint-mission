package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {
  private final UserRepository userRepository;
  private final UserService userService;

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(Role.ADMIN)) {
      return;
    }

    userService.createAdmin(
        "admin",
        "admin@example.com",
        "admin-password"
    );

    log.info("어드민 계정이 생성되었습니다.");
  }
}
