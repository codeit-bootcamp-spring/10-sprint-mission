package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.user.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.user.UserCreatedEvent;
import com.sprint.mission.discodeit.event.user.UserDeletedEvent;
import com.sprint.mission.discodeit.event.user.UserUpdatedEvent;
import com.sprint.mission.discodeit.exception.auth.PasswordEmptyException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidParameterException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
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
@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final PasswordEncoder passwordEncoder;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @CacheEvict(cacheNames = "users", allEntries = true)
  public UserResponse create(UserCreateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.userName(), "userName");
    requireNonNull(request.email(), "email");
    requireNonNull(request.password(), "password");

    if (request.password().isEmpty()) {
      throw new PasswordEmptyException();
    }

    if (userRepository.existsByUsername(request.userName())) {
      throw new UserAlreadyExistsException(request.userName());
    }

    if (userRepository.existsByEmail(request.email())) {
      throw new UserEmailAlreadyExistsException();
    }

    String encryptedPassword = passwordEncoder.encode(request.password());

    User user = new User(
        request.userName(),
        request.email(),
        encryptedPassword
    );

    BinaryContent profileImage = null;
    ProfileImageCreateRequest profileImageRequest = null;

    if (request.profileImage() != null) {
      profileImageRequest = request.profileImage();

      profileImage = new BinaryContent(
          profileImageRequest.fileName(),
          profileImageRequest.data().length,
          profileImageRequest.contentType()
      );

      user.updateProfileImage(profileImage);
    }

    User savedUser = userRepository.save(user);

    if (profileImage != null && profileImageRequest != null) {
      eventPublisher.publishEvent(
          new BinaryContentCreatedEvent(
              profileImage.getId(),
              profileImageRequest.data()
          )
      );
    }

    BinaryContent savedProfileImage = findProfileImageOrNull(savedUser);
    BinaryContentDto profileImageDto = savedProfileImage == null
        ? null
        : binaryContentMapper.toDto(savedProfileImage);

    boolean online = isOnline(savedUser.getId());

    UserDto userDto = userMapper.toDto(savedUser, online, profileImageDto);

    eventPublisher.publishEvent(new UserCreatedEvent(userDto));

    return userMapper.toResponse(savedUser, online, savedProfileImage);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse find(UUID userId) {
    requireNonNull(userId, "userId");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    BinaryContent profileImage = findProfileImageOrNull(user);
    boolean online = isOnline(user.getId());

    return userMapper.toResponse(user, online, profileImage);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          BinaryContent profileImage = findProfileImageOrNull(user);
          boolean online = isOnline(user.getId());

          return userMapper.toResponse(user, online, profileImage);
        })
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(cacheNames = "users")
  public List<UserDto> findAllDto() {
    return userRepository.findAll().stream()
        .map(user -> {
          BinaryContent profileImage = findProfileImageOrNull(user);
          BinaryContentDto profileDto =
              profileImage == null ? null : binaryContentMapper.toDto(profileImage);

          boolean online = isOnline(user.getId());

          return userMapper.toDto(user, online, profileDto);
        })
        .toList();
  }

  @PreAuthorize("@securityExpression.isSelf(#request.userId())")
  @Override
  @CacheEvict(cacheNames = "users", allEntries = true)
  public UserResponse update(UserUpdateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.userId(), "userId");

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));

    request.userName().ifPresent(newName -> {
      if (!user.getUsername().equals(newName) && userRepository.existsByUsername(newName)) {
        throw new UserAlreadyExistsException(request.userId());
      }
      user.updateName(newName);
    });

    request.email().ifPresent(newEmail -> {
      if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
        throw new UserEmailAlreadyExistsException();
      }
      user.updateEmail(newEmail);
    });

    request.password().ifPresent(newPassword -> {
      if (newPassword.isEmpty()) {
        throw new PasswordEmptyException();
      }

      String encryptedPassword = passwordEncoder.encode(newPassword);
      user.updatePassword(encryptedPassword);
    });

    BinaryContent newProfileImage = null;
    ProfileImageCreateRequest profileImageRequest = null;

    if (request.profileImage().isPresent()) {
      profileImageRequest = request.profileImage().get();

      newProfileImage = new BinaryContent(
          profileImageRequest.fileName(),
          profileImageRequest.data().length,
          profileImageRequest.contentType()
      );

      user.updateProfileImage(newProfileImage);
    }

    User savedUser = userRepository.save(user);

    if (newProfileImage != null && profileImageRequest != null) {
      eventPublisher.publishEvent(
          new BinaryContentCreatedEvent(
              newProfileImage.getId(),
              profileImageRequest.data()
          )
      );
    }

    BinaryContent profileImage = findProfileImageOrNull(savedUser);
    BinaryContentDto profileImageDto = profileImage == null
        ? null
        : binaryContentMapper.toDto(profileImage);

    boolean online = isOnline(savedUser.getId());

    UserDto userDto = userMapper.toDto(savedUser, online, profileImageDto);

    eventPublisher.publishEvent(new UserUpdatedEvent(userDto));

    return userMapper.toResponse(savedUser, online, profileImage);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Override
  @CacheEvict(cacheNames = "users", allEntries = true)
  public UserResponse updateRole(UserRoleUpdateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.userId(), "userId");
    requireNonNull(request.role(), "role");

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));

    UserRole oldRole = user.getRole();
    UserRole newRole = request.role();

    user.updateRole(newRole);

    User savedUser = userRepository.save(user);

    boolean roleChanged = oldRole != newRole;

    if (roleChanged) {
      eventPublisher.publishEvent(
          new RoleUpdatedEvent(
              savedUser.getId(),
              oldRole,
              newRole
          )
      );
    }

    jwtRegistry.invalidateJwtInformationByUserId(savedUser.getId());

    BinaryContent profileImage = findProfileImageOrNull(savedUser);
    BinaryContentDto profileImageDto = profileImage == null
        ? null
        : binaryContentMapper.toDto(profileImage);

    boolean online = isOnline(savedUser.getId());

    if (roleChanged) {
      UserDto userDto = userMapper.toDto(savedUser, online, profileImageDto);
      eventPublisher.publishEvent(new UserUpdatedEvent(userDto));
    }

    return userMapper.toResponse(savedUser, online, profileImage);
  }

  @PreAuthorize("@securityExpression.isSelf(#userId)")
  @Override
  @CacheEvict(cacheNames = "users", allEntries = true)
  public void delete(UUID userId) {
    requireNonNull(userId, "userId");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    UUID deletedUserId = user.getId();

    readStatusRepository.deleteByUserId(deletedUserId);
    userRepository.delete(user);

    eventPublisher.publishEvent(new UserDeletedEvent(deletedUserId));
  }

  @Override
  @Transactional(readOnly = true)
  public User findEntity(UUID userId) {
    requireNonNull(userId, "userId");

    return userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  private BinaryContent findProfileImageOrNull(User user) {
    if (user.getProfileImageId() == null) {
      return null;
    }

    return binaryContentRepository.findById(user.getProfileImageId())
        .orElseThrow(BinaryContentNotFoundException::new);
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new InvalidParameterException(name);
    }
  }

  private boolean isOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}