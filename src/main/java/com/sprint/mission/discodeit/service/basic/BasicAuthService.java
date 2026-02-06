package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.UserLogin;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public boolean login(UserLogin model) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.matchUsername(model.username()))
                .anyMatch(user -> user.matchPassword(model.password()));
    }
}
