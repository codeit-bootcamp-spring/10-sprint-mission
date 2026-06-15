package com.sprint.mission.discodeit.global.init;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.auth.infra.repository.UserCredentialRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

  private final UserRepository userRepository;
  private final UserCredentialRepository userCredentialRepository;
  private final PasswordEncoder passwordEncoder;
  @Value("${discodeit.admin.username}")
  private String adminUsername;
  @Value("${discodeit.admin.email}")
  private String adminEmail;
  @Value("${discodeit.admin.password}")
  private String adminPassword;

  @Transactional
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    boolean hasAdmin = userRepository.existsByRole(UserRole.ADMIN);
    if (hasAdmin) {
      return;
    }
    User admin = User.builder()
        .username(adminUsername)
        .email(adminEmail)
        .role(UserRole.ADMIN)
        .build();
    userRepository.save(admin);
    UserCredential userCredential = UserCredential.builder()
        .user(admin)
        .password(passwordEncoder.encode(adminPassword))
        .build();
    userCredentialRepository.save(userCredential);
  }
}
