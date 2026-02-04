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

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        // username으로 유저 조회 후 password 일치 확인
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(request.username()) && u.getPassword().equals(request.password()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 로그인 성공 시 해당 유저의 접속 상태 정보(UserStatus) 갱신
        // TODO: 이거 필요한 건지 고민 필요
        UserStatus status = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("유저 상태 데이터가 존재하지 않습니다."));
        status.renewActivity();  // 최근 접속 시간 업데이트
        userStatusRepository.save(status);

        // DTO(UserResponse)로 변환
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                status.isOnline()
        );
    }
}
