package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.admin.username}")
    private String adminUsername;

    @Value("${discodeit.admin.email}")
    private String adminEmail;

    @Value("${discodeit.admin.password}")
    private String adminPassword;

    @Bean
    public ApplicationRunner initializeAdminAccount() {
        return args -> {
            if (userRepository.existsByRole(Role.ADMIN)) {
                log.info("[ADMIN_INIT_SKIP] 이미 ADMIN 계정이 존재합니다.");
                return;
            }

            userRepository.findByUsername(adminUsername)
                            .ifPresentOrElse(existingAdmin ->
                                    {
                                        existingAdmin.updateRole(Role.ADMIN);
                                        userRepository.save(existingAdmin);
                                        log.info("[ADMIN_INIT_RECOVER] 기존 admin 계정의 권한을 ADMIN으로 복구했습니다.");
                                    }, () ->  {
                                User admin = new User(
                                        adminUsername,
                                        adminEmail,
                                        passwordEncoder.encode(adminPassword),
                                        null
                                );
                                admin.updateRole(Role.ADMIN);

                                UserStatus status = new UserStatus(admin, Instant.now());
                                admin.updateStatus(status);

                                userRepository.save(admin);
                                log.info("[ADMIN_INIT_SUCCESS] ADMIN 계정이 초기화되었습니다.");
                            });
        };
    }
}
