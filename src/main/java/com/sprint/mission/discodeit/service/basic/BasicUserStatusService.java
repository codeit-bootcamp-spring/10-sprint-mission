package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
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
  public UserStatusResponse create(UserStatusCreateRequest request) {

    //유저가 존재하지 않으면 예외
    userRepository.findById(request.userId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    //유저 상태가 이미 존재하면 에외
    userStatusRepository.findByUserId(request.userId())
        .ifPresent(status -> {
          throw new BusinessLogicException(ExceptionCode.USER_STATUS_ALREADY_EXISTS);
        });

    UserStatus status = new UserStatus(request.userId());
    userStatusRepository.save(status);

    return UserStatusResponse.of(status);
  }

  @Override
  public UserStatusResponse findById(UUID userStatusId) {
    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    return UserStatusResponse.of(status);
  }

  @Override
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(UserStatusResponse::of)
        .toList();
  }

  @Override
  public UserStatusResponse update(UUID id, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus status = userStatusRepository.findByUserId(id)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    status.updateOnline(newLastActiveAt);
    userStatusRepository.save(status);
    return UserStatusResponse.of(status);
  }

  @Override
  public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus status = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    status.updateOnline(newLastActiveAt);
    userStatusRepository.save(status);
    return UserStatusResponse.of(status);
  }

  @Override
  public void delete(UUID userStatusId) {
    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    userStatusRepository.delete(status);
  }
}
