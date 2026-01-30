package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.auth.LoginResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.CanNotLoginException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getName().equals(loginRequest.userName()) &&
                        u.getPassword().equals(loginRequest.password()))
                .findFirst()
                .orElseThrow(CanNotLoginException::new);

        UserStatus status = userStatusRepository.findByUserId(user.getId());


        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                status.isOnline(),
                status.getLastSeenAt(),
                user.getProfileImageId()
        );
    }
}
