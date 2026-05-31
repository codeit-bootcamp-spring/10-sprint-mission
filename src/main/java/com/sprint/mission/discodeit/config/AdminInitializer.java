package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
@Profile("dev")
@Configuration
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.admin.username:admin}")
    private String adminUsername;

    @Value("${discodeit.admin.email:admin@discodeit.com}")
    private String adminEmail;

    @Value("${discodeit.admin.password:Admin@123}")
    private String adminPassword;

    @Bean
    public ApplicationRunner initializeAdmin() {
        return args -> {
            boolean adminExists = userRepository.existsByRole(Role.ADMIN);
            if (!adminExists) {
                log.info("ADMIN 계정이 없어 자동 생성을 시작합니다.");

                String encodedPassword = passwordEncoder.encode(adminPassword);
                User admin = new User(adminUsername, adminEmail, encodedPassword, null);
                admin.updateRole(Role.ADMIN);

                userRepository.save(admin);

                log.info("ADMIN 계정 생성 완료: username={}", adminUsername);
            } else {
                log.debug("ADMIN 계정이 이미 존재합니다.");
            }
        };
    }
}
