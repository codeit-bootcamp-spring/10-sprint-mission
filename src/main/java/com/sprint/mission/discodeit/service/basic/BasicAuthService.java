package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.NonNull;
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
            throw new IllegalArgumentException("존재하지 않은 username 입니다 username: " + request.username());
        }
    }

    private @NonNull User getUserOrThrow(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 username: " + request.username()));
        return user;
    }

    private static void validatePassword(LoginRequest request, User user) {
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("일치하지않은 비밀번호 입니다. username: " + request.username());
        }
    }

    private @NonNull UserStatus getUserStatusOrThrow(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus 찾을 수 없습니다 userId: " + userId));
        return userStatus;
    }
}
