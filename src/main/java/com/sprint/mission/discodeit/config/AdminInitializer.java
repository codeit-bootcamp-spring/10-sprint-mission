package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    @Value("${discodeit.admin.password:admin1234!}")
    private String adminPassword;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            log.info("어드민 계정 자동 생성 진행");
            String adminUsername = "admin";
            User admin = new User(
                    adminUsername,
                    "admin@email.com",
                    passwordEncoder.encode(adminPassword),
                    null);
            admin.updateRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("어드민 계정 초기화 완료");
        } else {
            log.info("어드민 권한을 가진 사용자가 이미 존재하므로 초기화를 건너뜀");
        }
    }
}
