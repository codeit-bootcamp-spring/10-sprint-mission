package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;

  @Override
  public UserDto login(LoginRequest request) {
    // 유저 확인
    User user = userRepository.findByUsername(request.username())
        .orElseThrow(() -> new NoSuchElementException("일치하는 유저가 없습니다."));

    // 비밀번호 확인
    if (!user.getPassword().equals(request.password())) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    // 유저 상태 조회 및 업데이트
    UserStatus status = user.getUserStatus();
    if (status == null) {
      throw new NoSuchElementException("유저 상태 정보가 존재하지 않습니다.");
    }

    status.updateLastActiveAt(Instant.now());

    return toDto(user, status);
  }

  // 엔티티 -> DTO 변환
  private UserDto toDto(User user, UserStatus status) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null ? user.getProfile().getId() : null,
        status != null && status.isOnline()
    );
  }
}
