package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final ReadStatusRepository readStatusRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtRegistry jwtRegistry;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest request, MultipartFile profile) {
    validateDuplicateEmail(request.getEmail());
    validateDuplicateUserName(request.getUsername());

    BinaryContent profileImage = processImage(null, profile);
    User newUser = userMapper.toEntity(request);

    String encryptedPassword = passwordEncoder.encode(newUser.getPassword());
    newUser.updateEncodedPassword(encryptedPassword);

    if (profileImage != null) {
      newUser.update(null, null, null, profileImage);
    }

    User saved = userRepository.save(newUser);

    log.info("[SUCCESS] User Created: id={}, email={}", saved.getId(),
        saved.getEmail());

    return toDto(saved);
  }

  @Override
  public UserDto findById(UUID id) {
    return toDto(findUserEntityById(id));
  }

  @Override
  public List<UserDto> findAllUsers() {
    return userRepository.findAllWithDetails().stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  public UserDto findByEmail(String email) {

    User user = userRepository.findByEmail(email)
        .orElseThrow(() ->
            new RuntimeException("사용자를 찾을 수 없습니다.")
        );

    return toDto(user);
  }

  @Override
  public List<UserDto> findAllByChannelId(UUID channelId) {
    if (!channelRepository.existsById(channelId)) {
      throw new ChannelNotFoundException(channelId);
    }

    return readStatusRepository.findAllByChannelId(channelId).stream()
        .map(ReadStatus::getUser)
        .map(this::toDto)
        .toList();
  }

  @Transactional
  @Override
  @PreAuthorize("#userId == authentication.principal.userDto.id")
  public UserDto update(UUID userId, UserUpdateRequest request,
      MultipartFile profile) {
    User user = findUserEntityById(userId);

    if (request.getNewEmail() != null && !request.getNewEmail().equals(user.getEmail())) {
      validateDuplicateEmail(request.getNewEmail());
    }

    BinaryContent newProfile = processImage(user.getProfile(), profile);

    String finalUsername = (request.getNewUsername() != null && !request.getNewUsername().isBlank())
        ? request.getNewUsername().trim()
        : user.getUsername();

    String finalEmail = (request.getNewEmail() != null && !request.getNewEmail().isBlank())
        ? request.getNewEmail().trim()
        : user.getEmail();

    String finalPassword = (request.getNewPassword() != null && !request.getNewPassword().isBlank())
        ? passwordEncoder.encode(request.getNewPassword())
        : user.getPassword();

    user.update(
        finalUsername,
        finalEmail,
        finalPassword,
        newProfile
    );

    log.info("[SUCCESS] User Updated: id={}, email={}", userId, user.getEmail());

    return toDto(user);
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateUserRole(UserRoleUpdateRequest request) {
    User user = findUserEntityById(request.getUserId());

    user.updateRole(request.getNewRole());

    jwtRegistry.invalidateJwtInformationByUserId(user.getId());

    log.info("[SUCCESS] User Role Updated: id={}, newRole={}",
        user.getId(), user.getRole());

    return toDto(user);
  }

  @Transactional
  @Override
  @PreAuthorize("#userId == authentication.principal.userDto.id")
  public void delete(UUID userId) {

    User user = findUserEntityById(userId);

    readStatusRepository.deleteByUser(user);
    userRepository.delete(user);

    if (user.getProfile() != null) {
      binaryContentRepository.delete(user.getProfile());
    }

    log.info("[SUCCESS] User Deleted: id={}", userId);
  }

  // 이메일 중복 시 예외를 던져 가입 중단 (Fail-Fast)
  private void validateDuplicateEmail(String userEmail) {
    if (userRepository.findByEmail(userEmail).isPresent()) {
      throw new DuplicateEmailException(userEmail);
    }
  }

  // 이름 중복 시 예외를 던져 가입 중단 (Fail-Fast)
  private void validateDuplicateUserName(String username) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new DuplicateUsernameException(username);
    }
  }

  // 공통 User -> UserDto 변환
  private UserDto toDto(User user) {
    return userMapper.toDto(user, jwtRegistry);
  }

  // [헬퍼 메서드]: 반복되는 조회 및 예외 처리 공통화
  private User findUserEntityById(UUID id) {
    return userRepository.findWithDetailsById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }

  // [헬퍼 메서드]: 이미지 생성(createPublicChannel) 및 기존 이미지 수정(updateLastReadAt)
  private BinaryContent processImage(BinaryContent existingProfile, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return existingProfile;
    }

    // 기존 이미지가 있으면 삭제 (Update)
    if (existingProfile != null) {
      binaryContentRepository.delete(existingProfile);
    }

    try {
      // 새 이미지 저장
      BinaryContent newImage = new BinaryContent(
          file.getOriginalFilename(),
          file.getSize(),
          file.getContentType()
      );
      BinaryContent saved = binaryContentRepository.save(newImage);
      binaryContentStorage.put(saved.getId(), file.getBytes());
      return saved;
    } catch (IOException e) {
      throw new DiscodeitException(ErrorCode.FILE_SAVE_ERROR);
    }
  }
}
