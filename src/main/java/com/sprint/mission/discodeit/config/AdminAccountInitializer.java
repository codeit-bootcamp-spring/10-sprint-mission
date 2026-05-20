package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    if (userRepository.existsByRole(UserRole.ADMIN)) {
      return;
    }

    User admin = new User(
        "admin",
        "admin@admin.com",
        passwordEncoder.encode("admin")
    );

    admin.updateRole(UserRole.ADMIN);

    User savedAdmin = userRepository.save(admin);
    userStatusRepository.save(new UserStatus(savedAdmin, Instant.now()));
  }

}
