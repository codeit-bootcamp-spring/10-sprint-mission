package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.user.Role;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

  private final JPAUserRepository jpaUserRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(ApplicationArguments args) {
    if (!jpaUserRepository.existsByRole(Role.ADMIN)) {
      User admin = new User("admin", "admin@admin.com", passwordEncoder.encode("admin1234"), null,
          Role.ADMIN);
      jpaUserRepository.save(admin);
    }
  }
}
