package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.config.security.JwtRegistry;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
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
  private final PasswordEncoder passwordEncoder;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;
  private final SseService sseService;

  @Transactional
  @Override
  @CacheEvict(cacheNames = "users", allEntries = true)
  public UserDto create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
  ) {
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
          binaryContentRepository.save(binaryContent);
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent.getId(), bytes)
          );
          return binaryContent;
        })
        .orElse(null);

    String password = passwordEncoder.encode(userCreateRequest.password());
    User user = new User(username, email, password, nullableProfile);
    userRepository.save(user);

    UserDto createdUser = userMapper.toDto(user);
    sseService.broadcast("users.created", createdUser);
    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
    return createdUser;
  }

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

  @Cacheable(cacheNames = "users")
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

  @Transactional
  @Override
  @PreAuthorize("#p0 == authentication.principal.getUserDto().id()")
  @CacheEvict(cacheNames = "users", allEntries = true)
  public UserDto update(
      UUID userId,
      UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest
  ) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

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
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent.getId(), bytes)
          );
          return binaryContent;
        })
        .orElse(null);

    String newPassword = Optional.ofNullable(userUpdateRequest.newPassword())
        .map(passwordEncoder::encode)
        .orElse(null);
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    UserDto updatedUser = userMapper.toDto(user);
    sseService.broadcast("users.updated", updatedUser);
    log.info("사용자 수정 완료: id={}", userId);
    return updatedUser;
  }

  @Transactional
  @Override
  @PreAuthorize("#p0 == authentication.principal.getUserDto().id()")
  @CacheEvict(cacheNames = "users", allEntries = true)
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    UserDto deletedUser = userMapper.toDto(user);

    userRepository.deleteById(userId);
    sseService.broadcast("users.deleted", deletedUser);
    log.info("사용자 삭제 완료: id={}", userId);
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @CacheEvict(cacheNames = "users", allEntries = true)
  public UserDto updateRole(UUID userId, Role newRole) {
    log.debug("사용자 권한 수정 시작: id={}, newRole={}", userId, newRole);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role oldRole = user.getRole();
    user.updateRole(newRole);
    if (!oldRole.equals(newRole)) {
      eventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));
    }

    int invalidatedJwtCount = jwtRegistry.invalidateJwtInformationByUserId(userId);
    UserDto updatedUser = userMapper.toDto(user);
    sseService.broadcast("users.updated", updatedUser);

    log.info("사용자 권한 수정 완료: id={}, role={}, invalidatedJwtCount={}",
        userId, newRole, invalidatedJwtCount);
    return updatedUser;
  }
}
