package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    //1.이메일 중복 검증
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistException(Map.of("중복 검증하려는 이메일 정보",email));
    }
    if (userRepository.existsByUsername(username)) {
      throw new UserNameAlreadyExistException(Map.of("중복 검증하려는 사용자 이름 정보",username));
    }

    //2.프로필 저장
    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          log.debug("[BINARYCONTENT_CREATE] 프로필 저장: fileName={}", profileRequest.fileName());
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);

    //3. 회원 생성
    userRepository.save(user);
    log.info("[USER_CREATE] 사용자 생성 완료: userName= {}, email= {}", username, email);
    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(Map.of("조회 시도한 사용자 id 정보",userId)));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("조회 시도한 사용자 id 정보",userId)));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

      if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
          throw new EmailAlreadyExistException(Map.of("중복 검증하려는 이메일 정보",newEmail));
      }
      if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
          throw new UserNameAlreadyExistException(Map.of("중복 검증하려는 사용자 이름 정보",newUsername));
      }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
            log.debug("[BINARYCONTENT_UPDATE] profile: fileName= {}", profileRequest.fileName());
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    String profileName = nullableProfile != null ? nullableProfile.getFileName() : "기존유지";

    user.update(newUsername, newEmail, newPassword, nullableProfile);
    log.info("[USER_UPDATE]사용자 수정완료: userName= {},email= {},profile={} ", newUsername, newEmail, profileName);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {

    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(Map.of("조회 시도한 사용자 id 정보",userId));
    }

    userRepository.deleteById(userId);
    log.info("[USER_DELETE]회원삭제완료: userId = {}", userId);
  }
}
