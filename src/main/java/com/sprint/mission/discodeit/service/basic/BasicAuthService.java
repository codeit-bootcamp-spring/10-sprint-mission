package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.response.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        validateUsername(request);
        User user = getUserOrThrow(request);

        validatePassword(request, user);

        UserStatus userStatus = getUserStatusOrThrow(user.getId());
        userStatus.markActive();
        userStatusRepository.save(userStatus);

        return UserResponse.of(user, userStatus.getOnlineStatus());
    }

    private void validateUsername(LoginRequest request) {
        if (!userRepository.existsByUsername(request.username())) {
            throw new ApiException(ErrorCode.USER_NOT_FOUND,
                    "존재하지 않은 username 입니다 username: " + request.username());
        }
    }

    private User getUserOrThrow(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다 username: " + request.username()));
        return user;
    }

    private void validatePassword(LoginRequest request, User user) {
        if (!user.getPassword().equals(request.password())) {
            throw new ApiException(ErrorCode.INVALID_PASSWORD,
                    "일치하지않은 비밀번호 입니다. username: " + request.username());
        }
    }

    private UserStatus getUserStatusOrThrow(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_STATUS_NOT_FOUND,
                        "UserStatus 찾을 수 없습니다 userId: " + userId));
        return userStatus;
    }
}
