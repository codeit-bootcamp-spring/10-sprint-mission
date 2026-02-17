package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.LoginRequest;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public boolean login(LoginRequest request) throws IOException {
        return userRepository.filter(user -> user.matchUsername(request.username()))
                .anyMatch(user -> user.matchPassword(request.password()));
    }
}
