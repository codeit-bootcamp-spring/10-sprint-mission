package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.Role;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.init.email}")
    private String adminEmail;
    @Value("${admin.init.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initAdmin(){
        if(userRepository.existsByRole(Role.ADMIN)){
            log.info("ADMIN 계정이 이미 존재합니다.");
            return;
        }

        User admin = new User(
                "최고관리자",
                adminEmail,
                passwordEncoder.encode(adminPassword),
                Role.ADMIN
        );

        userRepository.save(admin);
        log.info("초기 ADMIN 계정이 생성되었습니다! [Email = {}]", adminEmail);
    }
}
