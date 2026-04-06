package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.InvalidFileFormatException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public UserDto createUser(UserCreateRequest request, MultipartFile file) {
    log.debug("User creation requested: {}", request.getUsername());
    if (userRepository.existsByName(request.getUsername())) {
      log.warn("User creation failed - Name already exists: {}", request.getUsername());
      throw new DuplicateUserException("username", request.getUsername());
    }
    if (userRepository.existsByEmail(request.getEmail())) {
      log.warn("User creation failed - Email already exists: {}", request.getEmail());
      throw new DuplicateUserException("email", request.getEmail());
    }

    BinaryContent profile = saveBinaryContent(file);

    User user = new User(request.getUsername(), request.getEmail(), request.getPassword(), profile);
    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    user.updateStatus(userStatus);
    userStatusRepository.save(userStatus);

    log.info("User created successfully: id={}, username={}", user.getId(), user.getName());
    return userMapper.toDto(user);
  }

  @Override
  public UserDto getUser(UUID id) {
    log.debug("Fetching user details: id={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User not found: id={}", id);
          return new UserNotFoundException(id);
        });
    return userMapper.toDto(user);
  }

  @Override
  public List<UserDto> getAllUsers() {
    log.debug("Fetching all users");
    return userRepository.findAll().stream()
        .map(userMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public UserDto updateUser(UUID userId, UserUpdateRequest request, MultipartFile file) {
    log.debug("User update requested: id={}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("Update failed - User not found: id={}", userId);
          return new UserNotFoundException(userId);
        });

    if (request.getNewUsername() != null && !request.getNewUsername().isBlank()) {
      String newName = request.getNewUsername();
      if (!user.getName().equals(newName) && userRepository.existsByName(newName)) {
        log.warn("Update failed - New username already exists: {}", newName);
        throw new DuplicateUserException("username", newName);
      }
      user.updateName(newName);
    }

    if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
      String newEmail = request.getNewEmail();
      if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
        log.warn("Update failed - New email already exists: {}", newEmail);
        throw new DuplicateUserException("email", newEmail);
      }
      user.updateEmail(newEmail);
    }

    if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
      user.updatePassword(request.getNewPassword());
    }

    if (file != null && !file.isEmpty()) {
      BinaryContent profile = saveBinaryContent(file);
      user.updateProfile(profile);
    }

    log.info("User updated successfully: id={}", userId);
    return userMapper.toDto(user);
  }

  @Transactional
  protected BinaryContent saveBinaryContent(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    validateContentType(file.getContentType());
    try {
      BinaryContent content = new BinaryContent(
          file.getOriginalFilename(),
          file.getContentType(),
          file.getSize()
      );
      binaryContentStorage.put(content.getId(), file.getBytes());
      log.debug("Binary content saved: id={}, filename={}", content.getId(), file.getOriginalFilename());
      return content;
    } catch (IOException e) {
      log.error("Failed to save binary content", e);
      throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
    }
  }

  @Override
  @Transactional
  public void deleteUser(UUID id) {
    log.debug("User deletion requested: id={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("Deletion failed - User not found: id={}", id);
          return new UserNotFoundException(id);
        });

    if (user.getProfile() != null) {
      binaryContentRepository.delete(user.getProfile());
    }

    userRepository.delete(user);
    log.info("User deleted successfully: id={}", id);
  }

  private void validateContentType(String contentType) {
    if (contentType == null || !ImageType.isAllowed(contentType)) {
      throw new InvalidFileFormatException(contentType);
    }
  }
}
