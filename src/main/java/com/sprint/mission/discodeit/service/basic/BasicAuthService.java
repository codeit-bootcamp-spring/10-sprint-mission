package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.CanNotLoginException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new IllegalArgumentException("loginRequest null이 될 수 없습니다.");
        }

        User user = userRepository.findAll().stream()
                .filter(u -> u.getName().equals(loginRequest.userName()) &&
                        u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(CanNotLoginException::new);

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                Optional.ofNullable(user.getProfileImageId())
        );
    }
}
