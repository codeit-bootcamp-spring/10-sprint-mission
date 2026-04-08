package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper mapper;

  @Transactional
  @Override
  public UserDto createUser(UserDto.UserCreateRequest userReq, MultipartFile profileImage)
      throws IOException {
    log.debug("[Service] 유저 생성 시작:");
    validateDuplicateUsername(userReq.username());
    validateDuplicateEmail(userReq.email());

    User user = new User(userReq.username(), userReq.password(), userReq.email());

    // userStatus 관련
    UserStatus status = new UserStatus();
    user.updateStatus(status);
    log.debug("[Service] UserStatus 저장 완료: id={}, userId={}", status.getId(), user.getId());

    // profile 이미지를 같이 추가하면
    processUpdateProfile(user, profileImage);
    userRepository.save(user);
    log.debug("[Service] 유저 저장 완료: id={}", user.getId());

    log.info("[Service] 유저 생성 성공: id={}", user.getId());
    return toDto(user);
  }

  @Override
  public List<UserDto> findAllUsers() {
    return userRepository.findAll().stream()
        .map(this::toDto).toList();
  }

  @Transactional
  @Override
  public UserDto updateUser(UUID uuid, UserDto.UserUpdateRequest userReq,
      MultipartFile profileImage) throws IOException {
    log.debug("[Service] 유저 수정 시작: id={}", uuid);
    User user = userRepository.findById(uuid)
        .orElseThrow(() -> new UserNotFoundException());

    // username과 mail 중복성 검사
    if (userReq.newUsername() != null
        && !Objects.equals(user.getUsername(), userReq.newUsername())) {
      validateDuplicateUsername(userReq.newUsername());
    }
    if (userReq.newEmail() != null
        && !Objects.equals(user.getEmail(), userReq.newEmail())) {
      validateDuplicateEmail(userReq.newEmail());
    }

    Optional.ofNullable(userReq.newUsername())
        .filter(StringUtils::hasText)
        .ifPresent(user::updateUserName);
    Optional.ofNullable(userReq.newPassword())
        .filter(StringUtils::hasText)
        .ifPresent(user::updatePassword);
    Optional.ofNullable(userReq.newEmail())
        .filter(StringUtils::hasText)
        .ifPresent(user::updateEmail);

    // 변경되는 프로필 이미지가 있으면
    processUpdateProfile(user, profileImage);
    userRepository.save(user);
    log.debug("[Service] 수정된 유저 저장 완료: id={}", user.getId());

    log.info("[Service] 유저 수정 성공: id={}", user.getId());
    return toDto(user);
  }

  @Transactional
  @Override
  public void deleteUser(UUID uuid) {
    log.debug("[Service] 유저 삭제 시작: id={}", uuid);
    if (!userRepository.existsById(uuid)) {
      throw new UserNotFoundException();
    }

    userRepository.deleteById(uuid);
    log.info("[Service] 유저 삭제 성공: id={}", uuid);
  }

  private void validateDuplicateUsername(String username) {
    if (userRepository.existsByUsername(username)) {
      throw new DuplicateUsernameException();
    }
  }

  private void validateDuplicateEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateEmailException();
    }
  }

  private UserDto toDto(User user) {
    return mapper.toDto(user);
  }

  private void processUpdateProfile(User user, MultipartFile profileImage) throws IOException {
    if (profileImage == null) {
      log.debug("[Service] 요청된 프로필 이미지 없음");
      return;
    }

    // BinaryContent 생성
    BinaryContent content = new BinaryContent(
        profileImage.getOriginalFilename(), profileImage.getSize(), profileImage.getContentType());
    binaryContentRepository.save(content);
    binaryContentStorage.put(content.getId(), profileImage.getBytes());
    log.debug("[Service] 프로필 이미지 저장 완료: contentId={}, userId={}",
        content.getId(), user.getId());

    user.updateProfile(content);
  }
}
