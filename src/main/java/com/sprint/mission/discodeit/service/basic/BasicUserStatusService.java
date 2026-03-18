package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UUID create(UUID userId) {
    requireNonNull(userId, "userId");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    if (userStatusRepository.findByUserId(userId) != null) {
      throw new BusinessLogicException(ErrorCode.CONFLICT);
    }

    UserStatus saved = userStatusRepository.save(new UserStatus(user, Instant.now()));
    return saved.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusResponse find(UUID id) {
    requireNonNull(id, "id");

    UserStatus status = userStatusRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND));

    return userStatusMapper.toResponse(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toResponse)
        .toList();
  }

  @Override
  public UserStatusResponse update(UUID id, Instant newLastActiveAt) {
    requireNonNull(id, "id");
    requireNonNull(newLastActiveAt, "newLastActiveAt");

    UserStatus status = userStatusRepository.findById(id)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND));

    status.updateLastSeenAt(newLastActiveAt);
    userStatusRepository.save(status);

    return userStatusMapper.toResponse(status);
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt) {
    requireNonNull(userId, "userId");
    requireNonNull(newLastActiveAt, "newLastActiveAt");

    userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    UserStatus status = userStatusRepository.findByUserId(userId);
    if (status == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    status.updateLastSeenAt(newLastActiveAt);
    userStatusRepository.save(status);

    return userStatusMapper.toResponse(status);
  }

  @Override
  public void deleteByUserId(UUID userId) {
    requireNonNull(userId, "userId");

    userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    if (userStatusRepository.findByUserId(userId) == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    userStatusRepository.deleteByUserId(userId);
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}