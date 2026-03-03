package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.dto.AuthServiceDTO.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        return userRepository.filter(user -> user.matchUsername(request.username()))
                .filter(user -> user.matchPassword(request.password()))
                .findFirst()
                .orElseThrow(() -> new APIException(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT))
                .toResponse(true);

    }
}
