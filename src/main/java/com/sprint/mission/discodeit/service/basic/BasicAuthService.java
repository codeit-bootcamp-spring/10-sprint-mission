package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserDto login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.getPassword().equals(password)) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                true
        );
    }
}
