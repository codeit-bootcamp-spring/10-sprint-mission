package com.sprint.mission.discodeit.Initializer;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User admin = new User(
                "admin",
                "admin@admin.com",
                passwordEncoder.encode("admin1234"),
                null
        );
        admin.updateRole(Role.ADMIN);

        userRepository.save(admin);
    }
}
