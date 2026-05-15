package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 (1)Spring Boot 시작
 (2)Bean 생성
 (3)DataSource 생성 (DB 연결)
 (4)ApplicationContext 초기화 완료
 (5)서버 실행 완료
 (6)CommandLineRunner 실행
 **/
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Value("${discodeit.admin.username}")
    private String username;
    @Value("${discodeit.admin.email}")
    private String email;
    @Value("${discodeit.admin.password}")
    private String password;

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        User admin = new User(
                username,
                email,
                password,
                null
        );

        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

    }
}
