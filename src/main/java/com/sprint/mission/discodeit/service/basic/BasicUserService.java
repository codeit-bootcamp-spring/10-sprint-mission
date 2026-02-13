package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService, ClearMemory {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final MessageRepository messageRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;

  @Override
  public UserResponseDto create(UserCreateDto request) {
    userRepository.findByName(request.userName())
        .ifPresent(u -> {
          throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        });
    userRepository.findByEmail(request.email())
        .ifPresent(u -> {
          throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        });
    User user = new User(request.userName(), request.email(), request.password(),
        request.profileId());

    UserStatus userStatus = new UserStatus(user.getId());
    userStatusRepository.save(userStatus);
    userRepository.save(user);
    return userMapper.toUserInfoDto(user, userStatus.updateStatusType());
  }

  @Override
  public UserResponseDto findById(UUID id) {
    User user = userRepository.findById(id).orElseThrow(()
        -> new IllegalArgumentException("실패 : 존재하지 않는 사용자 ID입니다."));
    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    return userMapper.toUserInfoDto(user, userStatus.updateStatusType());
  }

  @Override
  public List<UserResponseDto> findAll() {
    List<User> users = userRepository.findAll();
    Map<UUID, UserStatus> userStatusMap = userStatusRepository.getUserStatusMap();

    List<UserResponseDto> infoList
        = users.stream()
        .map(u -> {
          UserStatus userStatus = userStatusMap.get(u.getId());
          StatusType status =
              (userStatus == null) ? StatusType.OFFLINE : userStatus.getStatusType();
          return userMapper.toUserInfoDto(u, status);
        })
        .toList();
    return infoList;
  }

  @Override
  public UserResponseDto update(UUID id, UserUpdateDto request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
        .orElseGet(() -> new UserStatus(user.getId()));
    user.updateName(request.newName()); // 이름 변경

    if (request.profileId() != null && !request.profileId()
        .equals(user.getProfileId())) {  // 프로필 변경
      if (user.getProfileId() != null) {
        binaryContentRepository.delete(user.getProfileId());    // 기존 프로필 삭제
      }
      user.updateProfileId(request.profileId());
    }
    updateLastActiveTime(user.getId());   // 마지막 접속 시간 갱신
    userRepository.save(user);
    return userMapper.toUserInfoDto(user, userStatus.updateStatusType());
  }

  @Override
  public void delete(UUID id) {
    UserResponseDto userResponseDto = findById(id);

    // 프로필 삭제
    if (userResponseDto.profileId() != null) {
      binaryContentRepository.delete(userResponseDto.profileId());
    }

    // UserStatus 삭제
    userStatusRepository.deleteByUserId(userResponseDto.userId());

    // 사용자가 포함된 채널 정리
    channelRepository.deleteByUserId(userResponseDto.userId());

    // 사용자가 작성한 메시지 삭제
    messageRepository.deleteByUserId(id);

    // 사용자의 ReadStatus 삭제
    readStatusRepository.deleteByUserId(id);

    userRepository.delete(id);
  }

  @Override
  public void clear() {
    userRepository.clear();
  }

  @Override
  public void updateLastActiveTime(UUID id) {
    Optional<UserStatus> userStatus = userStatusRepository.findByUserId(id);
    userStatus.ifPresent(us -> {
      us.updateLastActiveTime();
      us.updateStatusType();
      userStatusRepository.save(us);
    });
  }


}
