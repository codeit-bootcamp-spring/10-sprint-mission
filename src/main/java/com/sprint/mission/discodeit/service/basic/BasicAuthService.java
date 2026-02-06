package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserWithOnlineResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserWithOnlineResponse login(LoginRequest loginRequest) {
        // 유저 검증, 없으면 예외 발생
        User user = userRepository.findByUserNameAndPassword(loginRequest.userName(), loginRequest.password())
                .orElseThrow(() -> new IllegalArgumentException("정확하지 않은 userName과 password입니다."));

        // 유저 존재하면
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않은 userStatus입니다."));

        // 온라인 상태 업데이트
        userStatus.updateLastOnlineTime(Instant.now());
        userStatusRepository.save(userStatus);

        return createUserInfo(user, userStatus);
    }

    private UserWithOnlineResponse createUserInfo(User user, UserStatus userStatus) {
        return new UserWithOnlineResponse(user.getId(), user.getEmail(), user.getUserName(), user.getNickName(),
                user.getBirthday(), user.getProfileId(), userStatus.isOnlineStatus());
    }
}
