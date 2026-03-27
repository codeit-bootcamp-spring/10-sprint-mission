package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public User create(String username, String email, String password, MultipartFile profileFile) {

    log.info("Creating new user with username: {}", username); // 유저 생성 시작 로그

    // username, email 중복 체크
    if (userRepository.existsByUsername(username)) {
      log.warn("User creation failed: Username {} already exists", username); // username 중복 로그
      throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
    }
    if (userRepository.existsByEmail(email)) {
      log.warn("User creation failed: Email already exists"); // email 중복 로그
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
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
        binaryContentRepository.save(profile);

        binaryContentStorage.put(profile.getId(), profileFile.getBytes());
      } catch (IOException e) {
        log.error("Profile image upload failed for file: {}", profileFile.getOriginalFilename(),
            e); // 프로필 사진 설정 실패 로그
        throw new RuntimeException("프로필 사진 설정 중 오류가 발생했습니다", e);
      }
    }

    // 유저 생성
    User user = new User(username, email, password, profile);
    UserStatus status = new UserStatus(user, Instant.now());
    user.assignUserStatus(status);

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

    List<User> users = userRepository.findAllWithProfileAndStatus();

    log.debug("Total users fetched: {}", users.size()); // 유저 전체 조회 성공 로그
    return users;
  }

  @Override
  @Transactional
  public User update(UUID id, String newUsername, String newEmail, String newPassword,
      MultipartFile profileFile) {
    log.info("Updating user with ID: {}", id); // 유저 업데이트 시작 로그
    User user = getOrThrowUser(id);

    // username 수정 + 중복 체크
    Optional.ofNullable(newUsername)
        .filter(username -> !username.equals(user.getUsername()))
        .ifPresent(username -> {
          if (userRepository.existsByUsername(username)) {
            log.warn("Update failed: Username {} already exists", username); // username 수정 실패 로그
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
          }
          user.updateName(username);
        });

    // 이메일 수정 + 중복 체크
    Optional.ofNullable(newEmail)
        .filter(email -> !email.equals(user.getEmail()))
        .ifPresent(email -> {
          if (userRepository.existsByEmail(email)) {
            log.warn("Update failed: Email already exists"); // 이메일 수정 실패 로그
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
          }
          user.updateEmail(email);
        });

    // 비밀번호 수정
    Optional.ofNullable(newPassword)
        .ifPresent(user::updatePassword);

    // 프로필 사진 수정
    if (profileFile != null && !profileFile.isEmpty()) {
      try {
        BinaryContent newImage = new BinaryContent(
            profileFile.getOriginalFilename(),
            profileFile.getSize(),
            profileFile.getContentType()
        );
        binaryContentRepository.save(newImage);

        binaryContentStorage.put(newImage.getId(), profileFile.getBytes());

        user.updateProfileImage(newImage);
      } catch (IOException e) {
        log.error("Failed to update profile image for user ID: {}", id, e); // 프로필 사진 업데이트 실패 로그
        throw new RuntimeException("프로필 사진 업데이트 중 오류가 발생했습니다.", e);
      }
    }

    log.info("User ID {} updated successfully", id); // 유저 업데이트 성공 로그
    return user;
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    log.info("Deleting user with ID: {}", id); // 유저 삭제 시작 로그

    User user = getOrThrowUser(id);
    userRepository.delete(user);

    log.info("User ID {} deleted successfully", id); // 유저 삭제 성공 로그
  }

  // --- Helper Methods ---

  // 유저 검증
  private User getOrThrowUser(UUID id) {
    return userRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("User not found with ID: {}", id); // 유저 조회 실패 로그
          return new NoSuchElementException("해당 유저를 찾을 수 없습니다.");
        });
  }
}
