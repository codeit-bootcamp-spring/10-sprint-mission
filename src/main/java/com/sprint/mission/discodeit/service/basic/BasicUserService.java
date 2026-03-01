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

import java.time.Instant;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final ReadStatusRepository readStatusRepository;

  @Override
  public com.sprint.mission.discodeit.dto.user.User create(UserCreateRequest request,
      MultipartFile profileFile) {
    // username, email 중복 체크
    validateDuplicateName(request.username());
    validateDuplicateEmail(request.email());

    // 프로필 사진 설정
    UUID profileId = null;
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        BinaryContent profileImage = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        binaryContentRepository.save(profileImage);
        profileId = profileImage.getId();
      } catch (IOException e) {
        throw new RuntimeException("프로필 이미지 저장 중 오류가 발생했습니다.", e);
      }
    }

    // 유저 생성
    User user = new User(
        request.username(),
        request.email(),
        request.password(),
        profileId
    );
    userRepository.save(user);

    // 유저 상태 생성
    UserStatus status = new UserStatus(user.getId(), true, Instant.now());
    userStatusRepository.save(status);

    return convertToUserResponse(user);
  }

  @Override
  public com.sprint.mission.discodeit.dto.user.User findById(UUID id) {
    User user = validateUserExists(id);
    return convertToUserResponse(user);
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(user -> convertToUserDto(user, getUserStatus(user.getId())))
        .toList();
  }

  @Override
  public com.sprint.mission.discodeit.dto.user.User update(UUID id, UserUpdateRequest request,
      MultipartFile profileFile) {
    User user = validateUserExists(id);

    // 이름 수정 + 중복 체크
    Optional.ofNullable(request.newUsername())
        .filter(username -> !username.equals(user.getUsername()))
        .ifPresent(username -> {
          validateDuplicateName(username);
          user.updateName(username);
        });

    // 이메일 수정 + 중복 체크
    Optional.ofNullable(request.newEmail())
        .filter(email -> !email.equals(user.getEmail()))
        .ifPresent(email -> {
          validateDuplicateEmail(email);
          user.updateEmail(email);
        });

    // 비밀번호 수정
    Optional.ofNullable(request.newPassword())
        .ifPresent(user::updatePassword);

    // 프로필 사진 수정
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        // 기존 프로필 사진 삭제
        if (user.getProfileId() != null) {
          binaryContentRepository.deleteById(user.getProfileId());
        }

        // 새 프로필 사진 저장
        BinaryContent newImage = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        binaryContentRepository.save(newImage);

        // 유저 정보에 새 이미지 ID 연결
        user.updateProfileImage(newImage.getId());
      } catch (IOException e) {
        throw new RuntimeException("프로필 사진 업데이트 중 오류가 발생했습니다.", e);
      }
    }

    userRepository.save(user);
    UserStatus status = getUserStatus(id);

    return convertToUserResponse(user);
  }

  @Override
  public void deleteById(UUID id) {
    User user = validateUserExists(id);

    // 유저가 참여하고 있는 채널 삭제
    readStatusRepository.deleteByUserId(id);

    // 유저 상태 삭제
    userStatusRepository.deleteByUserId(id);

    // 프로필 사진 삭제
    if (user.getProfileId() != null) {
      binaryContentRepository.deleteById(user.getProfileId());
    }

    // 유저 삭제
    userRepository.deleteById(id);
  }


  // 유저 상태 조회
  private UserStatus getUserStatus(UUID userId) {
    return userStatusRepository.findByUserId(userId).orElse(null);
  }

  // 유저 검증
  private User validateUserExists(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
  }

  // 사용자명 중복 체크
  private void validateDuplicateName(String name) {
    if (userRepository.findByUserName(name).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
    }
  }

  // 이메일 중복 체크
  private void validateDuplicateEmail(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }
  }

  // 엔티티 -> DTO 변환
  private UserDto convertToUserDto(User user, UserStatus status) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getProfileId(),
        status.isOnline()
    );
  }

  private com.sprint.mission.discodeit.dto.user.User convertToUserResponse(User user) {
    return new com.sprint.mission.discodeit.dto.user.User(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        user.getPassword(),
        user.getProfileId()
    );
  }
}
