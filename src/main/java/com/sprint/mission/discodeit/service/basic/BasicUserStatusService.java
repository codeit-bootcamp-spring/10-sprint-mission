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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    log.debug("유저 상태 생성 시작: userId={}", request.userId());
    //유저가 존재하지 않으면 예외
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

    //유저 상태가 이미 존재하면 에외
    userStatusRepository.findByUserId(request.userId())
        .ifPresent(status -> {
          throw new BusinessLogicException(ExceptionCode.USER_STATUS_ALREADY_EXISTS);
        });

    UserStatus userStatus = new UserStatus(user);
    userStatusRepository.save(userStatus);
    log.info("유저 상태 생성 완료: userId={}", userStatus.getId());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto findById(UUID userStatusId) {
    log.debug("유저 상태 조회 시작: userId={}", userStatusId);
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    log.debug("유저 상태 조회 완료: userStatusId={}", userStatusId);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    log.debug("유저 상태 목록 조회 시작");
    List<UserStatus> userStatuses = userStatusRepository.findAll();
    log.debug("유저 상태 목록 조회 완료: userStatusCount={}", userStatuses.size());
    return userStatuses.stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.debug("유저 상태 수정 시작: userStatusId={}", userStatusId);
    Instant newLastActiveAt = request.newLastActiveAt();
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    userStatus.updateOnline(newLastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("유저 상태 수정 완료: userStatusId={}", userStatus.getId());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.debug("유저 상태 수정 시작: userId={}", userId);
    Instant newLastActiveAt = request.newLastActiveAt();
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    userStatus.updateOnline(newLastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("유저 상태 수정 완료: userStatusId={}", userStatus.getId());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    log.debug("유저 상태 삭제 시작: userStatusId={}", userStatusId);
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    log.info("유저 상태 삭제 완료: userStatusId={}", userStatusId);
    userStatusRepository.delete(userStatus);
  }
}
