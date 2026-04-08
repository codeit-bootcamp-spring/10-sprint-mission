package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper mapper;

  @Transactional
  @Override
  public UserStatusDto createUserStatus(UserStatusDto.UserStatusCreateRequest createReq) {
    UUID userId = createReq.userId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException());
    userStatusRepository.findByUserId(userId)
        .ifPresent(u -> {
          throw new UserStatusAlreadyExistsException();
        });

    UserStatus status = new UserStatus();
    status.updateUser(user);
    userStatusRepository.save(status);

    return toResponse(status);
  }

  @Override
  public UserStatusDto findById(UUID uuid) {
    return userStatusRepository.findById(uuid)
        .map(this::toResponse)
        .orElseThrow(() -> new UserStatusNotFoundException());
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public UserStatusDto updateUserStatus(UUID uuid,
      UserStatusDto.UserStatusUpdateRequest updateReq) {
    UserStatus userStatus = userStatusRepository.findById(uuid)
        .orElseThrow(() -> new UserStatusNotFoundException());

    userStatus.updateLastActiveAt(updateReq.newLastActiveAt());
    userStatusRepository.save(userStatus);

    return toResponse(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateUserStatusByUserId(UUID userId,
      UserStatusDto.UserStatusUpdateRequest updateReq) {
    log.debug("[Service] UserStatus 수정 시작: userId={}", userId);
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new UserStatusNotFoundException());

    userStatus.updateLastActiveAt(updateReq.newLastActiveAt());
    userStatusRepository.save(userStatus);
    log.debug("[Service] 수정된 UserStatus 저장 완료: id={}, userId={}", userStatus.getId(), userId);

    log.info("[Service] UserStatus 수정 성공: id={}", userStatus.getId());
    return toResponse(userStatus);
  }

  @Transactional
  @Override
  public void deleteUserStatusById(UUID uuid) {
    UserStatus userStatus = userStatusRepository.findById(uuid)
        .orElseThrow(() -> new UserStatusNotFoundException());

    userStatusRepository.deleteById(uuid);
  }

  private UserStatusDto toResponse(UserStatus userStatus) {
    return mapper.toDto(userStatus);
  }
}
