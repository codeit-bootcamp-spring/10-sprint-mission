package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto login(LoginRequest request) {
        validateUsername(request);
        User user = getUserOrThrow(request);

        validatePassword(request, user);

        UserStatus userStatus = user.getStatus();
        userStatus.markActive();

        return userMapper.toDto(user);
    }

    private void validateUsername(LoginRequest request) {
        if (!userRepository.existsByUsername(request.username())) {
            throw new UserNotFoundException(
                    "존재하지 않은 username 입니다 username: " + request.username(),
                    Map.of("username", request.username())
            );
        }
    }

    private User getUserOrThrow(LoginRequest request) {
        return userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException(
                        "사용자를 찾을 수 없습니다 username: " + request.username(),
                        Map.of("username", request.username())
                ));
    }

    private void validatePassword(LoginRequest request, User user) {
        if (!user.getPassword().equals(request.password())) {
            throw new InvalidPasswordException(
                    "일치하지않은 비밀번호 입니다. username: " + request.username(),
                    Map.of("username", request.username())
            );
        }
    }
}
