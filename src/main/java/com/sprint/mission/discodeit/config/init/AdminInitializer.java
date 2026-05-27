package com.sprint.mission.discodeit.config.init;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AdminInitializer implements ApplicationRunner {

    private final AdminProperties adminProperties;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("[ADMIN_INITIALIZER] Admin 계정 초기화 시작");

        if (userRepository.existsByRole(Role.ADMIN)) {
            log.debug("[ADMIN_INITIALIZER] Admin 계정 이미 존재");
            return;
        }

        String username = adminProperties.getUsername();
        String email = adminProperties.getEmail();
        String password = adminProperties.getPassword();
        String encodedPassword = passwordEncoder.encode(password);

        // admin 생성 및 권한 부여
        User admin = new User(email, username, encodedPassword, null);
        admin.updateRole(Role.ADMIN);

        userRepository.save(admin);

        log.debug("[ADMIN_INITIALIZER] Admin 계정 초기화 완료: adminName={}", admin.getUsername());
    }
}
