package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.StatusType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserStatusMapper userStatusMapper;
  private final UserMapper userMapper;

  @Override
  public UserStatusInfoDto create(UserStatusCreateDto userStatusCreateDto) {
    User user = userRepository.findById(userStatusCreateDto.userId())
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    userStatusRepository.findByUserId(user.getId())
        .ifPresent(i -> {
          throw new IllegalArgumentException("해당 사용자의 UserStatus가 이미 있습니다.");
        });

    UserStatus userStatus = new UserStatus(user.getId());
    userStatusRepository.save(userStatus);
    return userStatusMapper.toUserInfoDto(userStatus);
  }

  @Override
  public UserStatusInfoDto findById(UUID id) {
    return userStatusMapper.toUserInfoDto(userStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 UserStatus가 없습니다.")));
  }

  @Override
  public List<UserStatusInfoDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toUserInfoDto)
        .toList();
  }

  @Override
  public UserStatusInfoDto update(UserStatusUpdateByIdDto userStatusUpdateByIdDto) {
    UserStatus userStatus = userStatusRepository.findById(userStatusUpdateByIdDto.id())
        .orElseThrow(() -> new IllegalArgumentException("해당 UserStatus가 없습니다."));

    userStatus.updateStatusType(userStatusUpdateByIdDto.statusType());
    userStatus.updateLastActiveTime();
    userStatusRepository.save(userStatus);
    return userStatusMapper.toUserInfoDto(userStatus);
  }

  // User 정보를 포함하여 반환
  @Override
  public UserResponseDto updateByUserId(UUID userId,
      UserStatusUpdateByUserIdDto userStatusUpdateByUserIdDto) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 상태정보가 없습니다."));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습ㄴ디ㅏ."));

    userStatus.updateStatusType(userStatusUpdateByUserIdDto.statusType());
    userStatus.updateLastActiveTime();
    UserStatus updatedStatus = userStatusRepository.save(userStatus);
    return userMapper.toUserInfoDto(user, updatedStatus.getStatusType());
  }

  @Override
  public void delete(UUID id) {
    userStatusRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 UserStatus가 없습니다."));
    userStatusRepository.delete(id);
  }
}
