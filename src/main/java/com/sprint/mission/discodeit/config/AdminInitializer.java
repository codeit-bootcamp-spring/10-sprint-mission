package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${discodeit.admin.username:admin}")
    private String adminUsername;

    @Value("${discodeit.admin.password:admin1234!}")
    private String adminPassword;

    @Value("${discodeit.admin.email:admin@gmail.com}")
    private String adminEmail;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 어드민 계정이 있는 경우 초기화하지 않음
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        User admin = new User(adminUsername, adminEmail,
                passwordEncoder.encode(adminPassword), null);
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("어드민 계정 초기화 완료: {}", adminUsername);

    }
}
