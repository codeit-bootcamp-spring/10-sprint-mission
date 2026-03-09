package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserStatusDto create(UserStatusCreateRequest request) {
    // 관련 유저가 존재하는지 확인
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));

    // 해당 유저의 UserStatus가 존재하는지 확인
    if (userStatusRepository.existsByUserId(request.userId())) {
      throw new IllegalStateException("이미 상태 정보가 존재합니다.");
    }

    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);

    return toDto(userStatus);
  }

  @Override
  public UserStatusDto findById(UUID id) {
    return toDto(getOrThrowUserStatus(id));
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID id, UserStatusUpdateRequest request) {
    UserStatus userStatus = getOrThrowUserStatus(id);
    userStatus.updateLastActiveAt(request.newLastActiveAt());
    return toDto(userStatus);
  }

  @Override
  public void deleteById(UUID id) {
    UserStatus userStatus = getOrThrowUserStatus(id);
    userStatusRepository.delete(userStatus);
  }

  // --- Helper Methods ---

  private UserStatus getOrThrowUserStatus(UUID id) {
    return userStatusRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 상태 정보를 찾을 수 없습니다."));
  }

  private UserStatusDto toDto(UserStatus userStatus) {
    return new UserStatusDto(
        userStatus.getId(),
        userStatus.getCreatedAt(),
        userStatus.getUpdatedAt(),
        userStatus.getUser().getId(),
        userStatus.getLastActiveAt(),
        userStatus.isOnline()
    );
  }
}
