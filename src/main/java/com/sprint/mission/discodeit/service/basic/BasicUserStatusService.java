package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentService binaryContentService;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;


  @Override
  @Transactional
  public UserDto create(UserCreateRequest request,
                        BinaryContentCreateRequest profileDto) {

    BinaryContent profile = (profileDto == null) ? null :
            binaryContentService.create(profileDto);
    // [수정됨] 불필요한 binaryContentRepository.save(profile); 제거

    User user = new User(
            request.username(),
            request.email(),
            request.password(),
            profile);

    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);

    return userMapper.toDto(user, true);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    User user = getUser(userId);
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

    boolean online = isOnline(userStatus.getLastActiveAt());

    return userMapper.toDto(user, online);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAll();

    // [N+1 문제 해결] 모든 유저 ID를 추출해서 한 번의 쿼리로 상태를 가져옴
    List<UUID> userIds = users.stream().map(User::getId).toList();

    // (주의: UserStatusRepository에 findAllByUserIdIn(List<UUID>) 메서드 추가 필요)
    Map<UUID, UserStatus> statusMap = userStatusRepository.findAllByUserIdIn(userIds).stream()
            .collect(Collectors.toMap(UserStatus::getUserId, us -> us));

    return users.stream()
            .map(user -> {
              UserStatus status = statusMap.get(user.getId());
              boolean online = status != null && isOnline(status.getLastActiveAt());
              return userMapper.toDto(user, online);
            })
            .toList();
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusRepository.save(userStatus);
  }

  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }

  private boolean isOnline(Instant lastOnlineAt){
    return lastOnlineAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
  }

  private User getUser(UUID userId){
    return userRepository.findById(userId)
            .orElseThrow(()-> new NoSuchElementException("존재하지 않는 사용자입니다."));
  }
}
