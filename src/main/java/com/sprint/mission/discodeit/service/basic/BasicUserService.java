package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadException;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PasswordEncoder passwordEncoder;
  private final JwtRegistry jwtRegistry;

  @Override
  @Transactional
  public User create(String username, String email, String password, MultipartFile profileFile) {

    log.info("Creating new user with username: {}", username); // 유저 생성 시작 로그

    // username, email 중복 체크
    if (userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException(Map.of("username", username));
    }
    if (userRepository.existsByEmail(email)) {
      throw new EmailAlreadyExistsException(Map.of("field", "email"));
    }

    // 프로필 사진 설정
    BinaryContent profile = null;
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        profile = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType()
        );
        binaryContentRepository.save(profile); // 메타데이터 저장

        // 메타데이터 저장 이벤트 발행
        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(profile.getId(), profileFile.getBytes()));
      } catch (IOException e) {
        throw new FileUploadException(Map.of(
            "username", username,
            "profileFileName",
            Objects.requireNonNullElse(profileFile.getOriginalFilename(), "unknown")
        ), e);
      }
    }

    // 평문 비밀번호를 BCrypt 해시로 변환하여 저장
    String encodedPassword = passwordEncoder.encode(password);

    // 유저 생성
    User user = new User(username, email, encodedPassword, profile);

    User savedUser = userRepository.save(user);

    log.info("User created successfully. ID: {}", savedUser.getId()); // 유저 생성 성공 로그
    return savedUser;
  }

  @Override
  public User findById(UUID id) {
    log.debug("Fetching user by ID: {}", id); // 유저 조회 시작 로그

    User user = getOrThrowUser(id);

    log.debug("User found: {}", user.getUsername()); // 유저 조회 성공 로그
    return user;
  }

  @Override
  public List<User> findAll() {
    log.info("Fetching all users list"); // 유저 전체 조회 시작 로그

    List<User> users = userRepository.findAllWithProfile();

    log.debug("Total users fetched: {}", users.size()); // 유저 전체 조회 성공 로그
    return users;
  }

  @Override
  @Transactional
  @PreAuthorize("#id == authentication.principal.id") // 수정하려는 타겟 ID와 현재 로그인한 사람의 ID가 일치할 때만 실행
  public User update(UUID id, String newUsername, String newEmail, String newPassword,
      MultipartFile profileFile) {
    log.info("Updating user with ID: {}", id); // 유저 업데이트 시작 로그
    User user = getOrThrowUser(id);

    // username 수정 + 중복 체크
    Optional.ofNullable(newUsername)
        .filter(username -> !username.equals(user.getUsername()))
        .ifPresent(username -> {
          if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException(Map.of("targetUserId", id));
          }
          user.updateName(username);
        });

    // 이메일 수정 + 중복 체크
    Optional.ofNullable(newEmail)
        .filter(email -> !email.equals(user.getEmail()))
        .ifPresent(email -> {
          if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(Map.of("targetUserId", id));
          }
          user.updateEmail(email);
        });

    // 비밀번호 수정
    Optional.ofNullable(newPassword)
        .ifPresent(pw -> user.updatePassword(passwordEncoder.encode(pw))); // 수정된 비밀번호 해시화

    // 프로필 사진 수정
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        BinaryContent newImage = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType()
        );
        BinaryContent savedImage = binaryContentRepository.save(newImage);

        // 메타 데이터 저장 이벤트 발행
        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(savedImage.getId(), profileFile.getBytes()));

        user.updateProfileImage(savedImage);
      } catch (IOException e) {
        throw new FileUploadException(Map.of(
            "targetUserId", id,
            "profileFileName",
            Objects.requireNonNullElse(profileFile.getOriginalFilename(), "unknown")
        ), e);
      }
    }

    log.info("User ID {} updated successfully", id); // 유저 업데이트 성공 로그
    return user;
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')") // 권한 검사
  public User updateUserRole(UUID id, Role newRole) {
    log.info("Updating role for user ID: {} to {}", id, newRole);

    // 유저 검증
    User user = getOrThrowUser(id);

    // 권한 변경 알림을 위해 권한 수정 전 현재 권한을 변수에 할당
    Role oldRole = user.getRole();

    // 권한 수정
    user.updateRole(newRole);

    // 권한이 변경된 사용자의 모든 토큰을 Registry에서 무효화
    jwtRegistry.invalidateJwtInformationByUserId(id);

    // 역할 변경 후 이벤트 발행
    eventPublisher.publishEvent(new RoleUpdatedEvent(id, oldRole, newRole));

    log.info("User ID {} role updated successfully to {}", id, newRole);
    return user;
  }

  @Override
  @Transactional
  @PreAuthorize("#id == authentication.principal.id") // 수정하려는 타겟 ID와 현재 로그인한 사람의 ID가 일치할 때만 실행
  public void deleteById(UUID id) {
    log.info("Deleting user with ID: {}", id); // 유저 삭제 시작 로그

    User user = getOrThrowUser(id);
    userRepository.delete(user);

    log.info("User ID {} deleted successfully", id); // 유저 삭제 성공 로그
  }

  // --- Helper Methods ---

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findByIdWithProfile(id)
        .orElseThrow(() -> new UserNotFoundException(Map.of("requestedUserId", id)));
  }
}
