package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.user.UserCreateEvent;
import com.sprint.mission.discodeit.event.user.UserDeleteEvent;
import com.sprint.mission.discodeit.event.user.UserUpdateEvent;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PasswordEncoder passwordEncoder;

  private final ApplicationEventPublisher eventPublisher;

  @CacheEvict(cacheManager = "redisCacheManager", cacheNames = "users", key = "'all'")
  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: {}", userCreateRequest);

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withUsername(username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          /// 메타데이터 DB 저장
          binaryContentRepository.save(binaryContent);
          /// S3 업로드
          //binaryContentStorage.put(binaryContent.getId(), bytes);

          ///BinaryContentStorage 호출하는 대신 BinaryContentCreatedEvent를 발행.
          eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes, null));

          return binaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();
    String encodedPassword = passwordEncoder.encode(password);

    User user = new User(username, email, encodedPassword, nullableProfile);
    userRepository.save(user);
    UserDto userDto = userMapper.toDto(user);
    eventPublisher.publishEvent(new UserCreateEvent(userDto));

    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
    return userDto;
  }

  /// 단일 사용자 조회도 캐시를 적용하려면 findAll()에 해당하는 캐시와 분리를 하는게 맞다.
  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.info("사용자 조회 완료: id={}", userId);
    return userDto;
  }

  /// 사용자 목록 조회.
  /// all - List<UserDto> 형태로 캐시에 저장.
  @Cacheable(cacheManager = "redisCacheManager", cacheNames = "users", key = "'all'")
  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    List<UserDto> userDtos = userRepository.findAllWithProfile()
        .stream()
        .map(userMapper::toDto)
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
    return userDtos;
  }

  /// 사용자 업데이트시 캐시 삭제
  @CacheEvict(cacheManager = "redisCacheManager", value = "users", key = "'all'")
  @PreAuthorize("principal.userDto.id == #userId")
  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          UserNotFoundException exception = UserNotFoundException.withId(userId);
          return exception;
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      throw UserAlreadyExistsException.withEmail(newEmail);
    }

    if (userRepository.existsByUsername(newUsername)) {
      throw UserAlreadyExistsException.withUsername(newUsername);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);

          ///BinaryContentStorage 호출하는 대신 BinaryContentCreatedEvent를 발행.
          eventPublisher.publishEvent(new BinaryContentCreatedEvent(binaryContent.getId(), bytes, user.getId()));
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    String encodedPassword = Optional.ofNullable(newPassword).map(passwordEncoder::encode)
        .orElse(user.getPassword());

    user.update(newUsername, newEmail, encodedPassword, nullableProfile);
    UserDto userDto = userMapper.toDto(user);
    eventPublisher.publishEvent(new UserUpdateEvent(userDto));
    log.info("사용자 수정 완료: id={}", userId);
    return userDto;
  }

  @CacheEvict(cacheManager = "redisCacheManager", value = "users", key = "'all'")
  @PreAuthorize("principal.userDto.id == #userId")
  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    User user = userRepository.findById(userId)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

    UserDto userDto = userMapper.toDto(user);

    userRepository.deleteById(userId);
    eventPublisher.publishEvent(new UserDeleteEvent(userDto));
    log.info("사용자 삭제 완료: id={}", userId);
  }
}
