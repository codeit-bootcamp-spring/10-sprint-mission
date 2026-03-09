package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.util.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest request, MultipartFile profileFile) {

    // username, email 중복 체크
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
    }
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    // 프로필 사진 설정
    BinaryContent profile = null;
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        profile = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        binaryContentRepository.save(profile);
      } catch (IOException e) {
        throw new RuntimeException("프로필 이미지 저장 중 오류 발생", e);
      }
    }

    // 유저 생성
    User user = new User(request.username(), request.email(), request.password(), profile);
    UserStatus status = new UserStatus(user, Instant.now());
    user.assignUserStatus(status);

    userRepository.save(user);
    return toDto(user, status);
  }

  @Override
  public UserDto findById(UUID id) {
    User user = getOrThrowUser(id);
    return toDto(user, user.getUserStatus());
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(user -> toDto(user, user.getUserStatus()))
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID id, UserUpdateRequest request, MultipartFile profileFile) {
    User user = getOrThrowUser(id);

    // 이름 수정 + 중복 체크
    Optional.ofNullable(request.newUsername())
        .filter(username -> !username.equals(user.getUsername()))
        .ifPresent(username -> {
          if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
          }
          user.updateName(username);
        });

    // 이메일 수정 + 중복 체크
    Optional.ofNullable(request.newEmail())
        .filter(email -> !email.equals(user.getEmail()))
        .ifPresent(email -> {
          if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
          }
          user.updateEmail(email);
        });

    // 비밀번호 수정
    Optional.ofNullable(request.newPassword())
        .ifPresent(user::updatePassword);

    // 프로필 사진 수정
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        BinaryContent newImage = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        binaryContentRepository.save(newImage);
        user.updateProfileImage(newImage);
      } catch (IOException e) {
        throw new RuntimeException("프로필 업데이트 오류", e);
      }
    }

    return toDto(user, user.getUserStatus());
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    User user = getOrThrowUser(id);
    userRepository.delete(user);
  }

  // --- Helper Methods ---

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
  }

  // 엔티티 -> DTO 변환
  private UserDto toDto(User user, UserStatus status) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfile() != null ? user.getProfile().getId() : null,
        status != null && status.isOnline()
    );
  }
}
