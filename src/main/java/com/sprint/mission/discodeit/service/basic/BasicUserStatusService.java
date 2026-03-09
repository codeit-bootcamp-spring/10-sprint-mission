package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
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
  public UserStatusDto create(UserStatusCreateRequest request) {

    //유저가 존재하지 않으면 예외
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    //유저 상태가 이미 존재하면 에외
    userStatusRepository.findByUserId(request.userId())
        .ifPresent(status -> {
          throw new BusinessLogicException(ExceptionCode.USER_STATUS_ALREADY_EXISTS);
        });

    UserStatus status = new UserStatus(user);
    userStatusRepository.save(status);

    return userStatusMapper.toDto(status);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto findById(UUID userStatusId) {
    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    return userStatusMapper.toDto(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    status.updateOnline(newLastActiveAt);
    userStatusRepository.save(status);
    return userStatusMapper.toDto(status);
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    status.updateOnline(newLastActiveAt);
    userStatusRepository.save(status);
    return userStatusMapper.toDto(status);
  }

  @Override
  public void delete(UUID userStatusId) {
    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    userStatusRepository.delete(status);
  }
}
