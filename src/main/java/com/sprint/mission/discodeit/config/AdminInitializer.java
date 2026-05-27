package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // 있으면 생략
        if (userRepository.existsByRole(Role.ADMIN)){
            return;
        }

        User admin = new User(
                "admin",
                "admin@naver.com",
                passwordEncoder.encode("1q2w3e4r!"),
                null
        );

        admin.updateRole(Role.ADMIN);

        userRepository.save(admin);
    }
}
