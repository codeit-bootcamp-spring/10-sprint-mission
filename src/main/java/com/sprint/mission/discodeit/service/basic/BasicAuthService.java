package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService {
    private final UserRepository userRepository;

    public User login(AuthLoginRequestDto request) {
        String username = request.username();
        String password = request.password();
        User user = userRepository.findAll().stream()
                .filter(u -> username.equals(u.getUserName()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("로그인 실패"));

        if (!password.equals(user.getUserPassword())) {
            throw new IllegalArgumentException("로그인 실패");
        }
        return  user;
    }
}
