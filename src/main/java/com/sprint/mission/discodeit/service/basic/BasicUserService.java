package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  //모든 멤버를 공개채널에 추가하기 위해..
  private final ReadStatusRepository readStatusRepository;
  private final ChannelRepository channelRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public UserDto create(UserCreateRequest dto,
      Optional<BinaryContentCreateDto> binaryContentCreateDto) {
    log.debug("사용자 생성 시도: email={}, username={}", dto.email(), dto.username());
    validateEmail(dto.email());
    validateUsername(dto.username());
    //프로필 사진
    log.debug("사용자 생성 중: 프로필 사진 생성");
    BinaryContent profile = null;
    if (binaryContentCreateDto.isPresent()) {
      profile = binaryContentMapper.toEntity(binaryContentCreateDto.get());
    }
    User user = userMapper.toEntity(dto, profile);
    UserStatus userStatus = UserStatus.create(user, Instant.now());
    //모든 공개채널에 대한 읽기 상태 저장
    log.debug("사용자 생성 중: 사용자 저장 시도");
    userRepository.save(user);
    log.debug("사용자 생성 중: 모든 공개 채널에 멤버로 추가");
    List<ReadStatus> readStatuses = channelRepository.findAllPublic().stream()
        .map(c -> ReadStatus.create(user, c, Instant.EPOCH))
        .toList();
    readStatusRepository.saveAll(readStatuses);
    if (binaryContentCreateDto.isPresent()) {
      binaryContentStorage.put(profile.getId(), binaryContentCreateDto.get().bytes());
      log.debug("프로필 이미지 생성: userId={}, profileId={}", user.getId(), profile.getId());
    }
    log.info("사용자 생성 성공: userId={} createdFields=[username={}, email={}, profile={}, password={}]",
        user.getId(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null,
        user.getPassword() != null
    );
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    User user = get(userId);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    List<User> users = userRepository.findAllFetchUserInfo();
    List<UserDto> response = new ArrayList<>();
    users.forEach(u -> response.add(userMapper.toDto(u)));
    return response;
  }

  @Override
  public UserDto update(UUID userId, UserUpdateRequest dto,
      Optional<BinaryContentCreateDto> binaryContentCreateDto) {
    log.debug("사용자 수정 시도: userId={}", userId);
    User user = userRepository.findByIdFetchUserInfo(userId)
        .orElseThrow(() -> new UserNotFoundException().addDetail("userId", userId));
    if (dto.email() != null && !user.getEmail().equals(dto.email())) { //변경하는 경우만 검증
      validateEmail(dto.email());
    }
    if (dto.username() != null && !user.getUsername().equals(dto.username())) {
      validateUsername(dto.username());
    }
    BinaryContent profile = null;
    log.debug("사용자 수정 중: 프로필 사진 생성");
    if (binaryContentCreateDto.isPresent()) {
      profile = binaryContentMapper.toEntity(binaryContentCreateDto.get());
      binaryContentRepository.save(profile);//profile id가 필요하기 때문에
    }
    user.update(dto.username(), dto.email(), dto.password(), profile);
    if (binaryContentCreateDto.isPresent()) {//위에서 생성하면 업데이트 실패시 스토리지 저장을 되돌릴수 없기 때문
      binaryContentStorage.put(profile.getId(), binaryContentCreateDto.get().bytes());
      log.debug("새로운 프로필 이미지 생성: userId={}, profileId={}", user.getId(), profile.getId());
    }
    log.info("사용자 수정 성공: userId={}, updatedFields=[username={}, email={}, profile={}, password={}]",
        userId,
        dto.username(),
        dto.email(),
        profile != null,
        dto.password() != null
    );
    return userMapper.toDto(user);
  }

  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시도: userId={}", userId);
    User user = get(userId);
    log.debug("사용자 삭제 중: 읽기 상태에서 사용자 제거");
    readStatusRepository.deleteByUserId(userId);//삭제된 사용자를 공개채널 멤버나 프라이빗 채널 멤버에서 제거(벌크)
    userRepository.deleteById(userId);
    log.info("사용자 삭제 성공: userId={}", userId);
  }

  private void validateEmail(String email) {
    log.debug("이메일 유효성 검사: email={}", email);
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistException().addDetail("email", email);
    }
  }

  private void validateUsername(String username) {
    log.debug("사용자 이름 유효성 검사: username={}", username);
    if (userRepository.existsByUsername(username)) {
      throw new UserNameAlreadyExistException().addDetail("username", username);
    }
  }

  private User get(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> {
          return new UserNotFoundException().addDetail("userId", userId);
        });
  }
}
