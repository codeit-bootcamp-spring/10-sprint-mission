package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    if (userRepository.findByUsername("admin").isPresent()) {
      return;
    }
    // userStatus 추가 안해서 에러 발생.
    User admin = new User("admin", "admin@discodeit.com", passwordEncoder.encode("admin123"), null);

    admin.updateRole(Role.ADMIN);

    new UserStatus(admin, Instant.now());

    userRepository.save(admin);
  }
}
