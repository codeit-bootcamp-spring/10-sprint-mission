package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
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

@Transactional
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto create(UserStatusCreateRequest userStatusCreateRequest) {
    // 유저 검색
    User user = userRepository.findById(userStatusCreateRequest.userId())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 유저 상태가 이미 존재한다면 예외 발생
    if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
      throw new BusinessException(ErrorCode.USER_STATUS_ALREADY_EXISTS);
    }

    Instant lastActiveAt = userStatusCreateRequest.lastActiveAt();
    // 유저 상태 생성
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional(readOnly = true)
  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepository.findDetailById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAllWithUser().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest userStatusUpdateRequest) {
    Instant newLastActiveAt = userStatusUpdateRequest.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findDetailById(userStatusId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));

    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId,
      UserStatusUpdateRequest userStatusUpdateRequest) {
    Instant newLastActiveAt = userStatusUpdateRequest.newLastActiveAt();

    // 유저 상태 조회
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));

    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    UserStatus userStatus = userStatusRepository.findDetailById(userStatusId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_STATUS_NOT_FOUND));

    User user = userStatus.getUser();
    if (user != null) {
      user.removeUserStatus();
      return;
    }

    userStatusRepository.delete(userStatus);
  }
}