package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.common.InvalidParameterException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.StatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        .orElseThrow(() -> new UserNotFoundException(userId));

    if (userStatusRepository.findByUserId(userId) != null) {
      throw new InvalidParameterException(
          "userId",
          "이미 사용자 상태 정보가 존재합니다."
      );
    }

    UserStatus saved = userStatusRepository.save(new UserStatus(user, Instant.now()));
    log.info("유저상태가 성공적으로 생성 되었습니다. id: {}", saved.getId());
    return saved.getId();
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusResponse find(UUID id) {
    requireNonNull(id, "id");

    UserStatus status = userStatusRepository.findById(id)
        .orElseThrow(() -> new StatusNotFoundException(id));

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
        .orElseThrow(() -> new StatusNotFoundException(id));

    status.updateLastSeenAt(newLastActiveAt);
    userStatusRepository.save(status);
    log.info("유저 상태가 성공적으로 수정되었습니다. id = {}", id);
    return userStatusMapper.toResponse(status);
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId, Instant newLastActiveAt) {
    requireNonNull(userId, "userId");
    requireNonNull(newLastActiveAt, "newLastActiveAt");

    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    UserStatus status = userStatusRepository.findByUserId(userId);
    if (status == null) {
      throw new StatusNotFoundException(userId);
    }

    status.updateLastSeenAt(newLastActiveAt);
    userStatusRepository.save(status);

    log.info("유저 상태가 성공적으로 수정되었습니다. userId = {}", userId);
    return userStatusMapper.toResponse(status);
  }

  @Override
  public void deleteByUserId(UUID userId) {
    requireNonNull(userId, "userId");

    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    if (userStatusRepository.findByUserId(userId) == null) {
      throw new StatusNotFoundException(userId);
    }

    userStatusRepository.deleteByUserId(userId);
    log.info("유저상태가 성공적으로 삭제되었습니다. id  = {}", userId);
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new InvalidParameterException(name);
    }
  }
}