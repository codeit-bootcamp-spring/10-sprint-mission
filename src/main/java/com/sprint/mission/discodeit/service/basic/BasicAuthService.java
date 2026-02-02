package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.UserLogin;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserService userService;

    @Override
    public void login(UserLogin model) {
        userService.find(model.username(), model.password());
    }
}
