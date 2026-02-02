package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserResponse login(LoginRequest request) {
        // 로그인을 위한 필수 검증
        User user = validateLoginRequest(request);

        // 유저 상태 조회
        UserStatus userStatus = userStatusRepository.findById(user.getId())
                .orElse(null);

        return UserResponse.from(user, userStatus);
    }

    private User validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new RuntimeException("요청이 필요합니다.");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new RuntimeException("이메일을 입력해주세요.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("비밀번호를 입력해주세요.");
        }

        // 이메일과 비밀번호가 일치하는 유저 조회
        return userRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));
    }
}
