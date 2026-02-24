// BasicUserStatusService.java
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UUID create(UUID userId) {
    requireNonNull(userId, "userId");

    if (userRepository.findById(userId) == null) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }

    if (userStatusRepository.findByUserId(userId) != null) {
      throw new BusinessLogicException(ErrorCode.CONFLICT);
    }

    UserStatus saved = userStatusRepository.save(new UserStatus(userId, Instant.now()));
    return saved.getId();
  }

  @Override
  public UserStatusResponse find(UUID id) {
    requireNonNull(id, "id");

    UserStatus status = userStatusRepository.findById(id);
    if (status == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    return toDto(status);
  }

  @Override
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public UserStatusResponse update(UUID id, Instant newLastActiveAt) {
    requireNonNull(id, "id");
    requireNonNull(newLastActiveAt, "newLastActiveAt");

    UserStatus status = userStatusRepository.findById(id);
    if (status == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    status.updateLastSeenAt(newLastActiveAt);
    userStatusRepository.save(status);

    return toDto(status);
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt) {
    requireNonNull(userId, "userId");
    requireNonNull(newLastActiveAt, "newLastActiveAt");

    if (userRepository.findById(userId) == null) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }

    UserStatus status = userStatusRepository.findByUserId(userId);
    if (status == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    // 요구사항: now()가 아니라 요청으로 받은 시간 반영
    status.updateLastSeenAt(newLastActiveAt);
    userStatusRepository.save(status);

    return toDto(status);
  }

  @Override
  public void deleteByUserId(UUID userId) {
    requireNonNull(userId, "userId");

    if (userRepository.findById(userId) == null) {
      throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);
    }
    if (userStatusRepository.findByUserId(userId) == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    userStatusRepository.deleteByUserId(userId);
  }

  private UserStatusResponse toDto(UserStatus status) {
    return new UserStatusResponse(
        status.getId(),
        status.getCreatedAt(),
        status.getUpdatedAt(),
        status.getUserId(),
        status.getLastSeenAt(),   // DTO 필드명은 lastActiveAt
        status.isOnline()
    );
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}