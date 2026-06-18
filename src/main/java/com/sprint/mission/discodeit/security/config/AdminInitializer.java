package com.sprint.mission.discodeit.security.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    User admin = userRepository.findByUsername("admin")
        .orElse(null);

    if (admin == null) {
      try {
        admin = userRepository.save(
            new User(
                "admin",
                "admin@discodeit.com",
                passwordEncoder.encode("admin1234")
            )
        );
      } catch (DataIntegrityViolationException ignored) {
        admin = userRepository.findByUsername("admin")
            .orElseThrow();
      }
    }

    if (admin.getRole() != Role.ADMIN) {
      admin.updateRole(Role.ADMIN);
      userRepository.save(admin);
    }
  }
}
