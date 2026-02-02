package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    // Service 간 의존성 주입 지양 → Repository만 주입
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    @Transactional
    public UserResponse login(LoginRequest request) {
        // 1. username으로 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 발견 실패"));

        // 2. 비밀번호 검증
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("패스워드 불일치");
        }

        // 3. UserStatus 업데이트 (ONLINE 상태로 변경)
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found for user: " + user.getId()));

        userStatus.updateStatus("ONLINE");
        userStatus. updateLastActiveAt(Instant.now());
        userStatusRepository.save(userStatus);

        // 4. UserResponse 반환 (패스워드 제외, 온라인 상태 포함)
        return UserResponse.from(user, userStatus);
    }
}
