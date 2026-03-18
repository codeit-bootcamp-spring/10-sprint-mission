package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final ReadStatusRepository readStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public UserResponse create(UserCreateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.userName(), "userName");
    requireNonNull(request.email(), "email");
    requireNonNull(request.password(), "password");

    if (request.password().isEmpty()) {
      throw new BusinessLogicException(ErrorCode.PASSWORD_EMPTY);
    }

    if (userRepository.existsByUsername(request.userName())) {
      throw new BusinessLogicException(ErrorCode.DUPLICATION_USER);
    }

    if (userRepository.existsByEmail(request.email())) {
      throw new BusinessLogicException(ErrorCode.DUPLICATION_EMAIL);
    }

    User user = new User(request.userName(), request.email(), request.password());
    new UserStatus(user, Instant.now());

    if (request.profileImage() != null) {
      ProfileImageCreateRequest imgReq = request.profileImage();
      BinaryContent image = new BinaryContent(
          imgReq.fileName(),
          imgReq.data().length,
          imgReq.contentType()
      );
      binaryContentStorage.put(image.getId(), imgReq.data());

      user.updateProfileImage(image);
    }

    User savedUser = userRepository.save(user);
    BinaryContent profileImage = savedUser.getProfileImage();

    return userMapper.toResponse(savedUser, savedUser.getStatus(), profileImage);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse find(UUID userId) {
    requireNonNull(userId, "userId");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    UserStatus status = userStatusRepository.findByUserId(userId);
    if (status == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    BinaryContent profileImage = findProfileImageOrNull(user);

    return userMapper.toResponse(user, status, profileImage);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          UserStatus status = userStatusRepository.findByUserId(user.getId());
          if (status == null) {
            throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
          }

          BinaryContent profileImage = findProfileImageOrNull(user);

          return userMapper.toResponse(user, status, profileImage);
        })
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAllDto() {
    return userRepository.findAll().stream()
        .map(user -> {
          UserStatus status = userStatusRepository.findByUserId(user.getId());
          if (status == null) {
            throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
          }

          BinaryContent profileImage = findProfileImageOrNull(user);
          BinaryContentDto profileDto =
              profileImage == null ? null : binaryContentMapper.toDto(profileImage);

          return userMapper.toDto(user, status, profileDto);
        })
        .toList();
  }

  @Override
  public UserResponse update(UserUpdateRequest request) {
    requireNonNull(request, "request");
    requireNonNull(request.userId(), "userId");

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    request.userName().ifPresent(newName -> {
      if (!user.getUsername().equals(newName) && userRepository.existsByUsername(newName)) {
        throw new BusinessLogicException(ErrorCode.DUPLICATION_USER);
      }
      user.updateName(newName);
    });

    request.email().ifPresent(newEmail -> {
      if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
        throw new BusinessLogicException(ErrorCode.DUPLICATION_EMAIL);
      }
      user.updateEmail(newEmail);
    });

    request.password().ifPresent(newPassword -> {
      if (newPassword.isEmpty()) {
        throw new BusinessLogicException(ErrorCode.PASSWORD_EMPTY);
      }
      user.updatePassword(newPassword);
    });

    request.profileImage().ifPresent(imgReq -> {
      BinaryContent newImage = new BinaryContent(
          imgReq.fileName(),
          imgReq.data().length,
          imgReq.contentType()
      );
      user.updateProfileImage(newImage);
    });

    User savedUser = userRepository.save(user);

    UserStatus status = userStatusRepository.findByUserId(savedUser.getId());
    if (status == null) {
      throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
    }

    BinaryContent profileImage = findProfileImageOrNull(savedUser);

    return userMapper.toResponse(savedUser, status, profileImage);
  }

  @Override
  public void delete(UUID userId) {
    requireNonNull(userId, "userId");

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

    readStatusRepository.deleteByUserId(userId);
    userRepository.delete(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User findEntity(UUID userId) {
    requireNonNull(userId, "userId");

    return userRepository.findById(userId)
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
  }

  private BinaryContent findProfileImageOrNull(User user) {
    if (user.getProfileImageId() == null) {
      return null;
    }

    return binaryContentRepository.findById(user.getProfileImageId())
        .orElseThrow(() -> new BusinessLogicException(ErrorCode.BINARY_CONTENT_NOT_FOUND));
  }

  private static <T> void requireNonNull(T value, String name) {
    if (value == null) {
      throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
    }
  }
}