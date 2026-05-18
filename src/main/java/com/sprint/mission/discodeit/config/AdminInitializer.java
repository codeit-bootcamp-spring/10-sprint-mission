package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    User admin = userRepository.findByUsername("admin")
        .orElseGet(() -> {
          User newAdmin = new User(
              "admin",
              "admin@discodeit.com",
              passwordEncoder.encode("admin1234")
          );

          return userRepository.save(newAdmin);
        });

    if (admin.getRole() != Role.ADMIN) {
      admin.updateRole(Role.ADMIN);
    }
  }
}
