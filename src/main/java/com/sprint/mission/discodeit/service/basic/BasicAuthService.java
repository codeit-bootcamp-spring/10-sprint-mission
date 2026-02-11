package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public UserResponse login(LoginRequest request) {
        // 유저 확인
        User user = userRepository.findByName(request.name())
                .orElseThrow(() -> new NoSuchElementException("일치하는 유저가 없습니다."));

        // 비밀번호 확인
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 유저 상태 조회 및 업데이트
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저 상태 정보가 존재하지 않습니다."));

        status.updateLastActiveAt();
        userStatusRepository.save(status);

        return convertToResponse(user, status);
    }

    // 엔티티 -> DTO 변환
    private UserResponse convertToResponse(User user, UserStatus status) {
        boolean isOnline = (status != null) && status.isOnline();
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileId(),
                isOnline,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
