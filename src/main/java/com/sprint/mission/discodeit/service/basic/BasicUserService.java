package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public User create(String username, String email, String password, MultipartFile profileFile) {

    // username, email 중복 체크
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
    }
    if (userRepository.existsByEmail(email)) {
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
        throw new RuntimeException("프로필 사진 설정 오류", e);
      }
    }

    // 유저 생성
    User user = new User(username, email, password, profile);
    UserStatus status = new UserStatus(user, Instant.now());
    user.assignUserStatus(status);

    return userRepository.save(user);
  }

  @Override
  public User findById(UUID id) {
    return getOrThrowUser(id);
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  @Transactional
  public User update(UUID id, String newUsername, String newEmail, String newPassword,
      MultipartFile profileFile) {
    User user = getOrThrowUser(id);

    // 이름 수정 + 중복 체크
    Optional.ofNullable(newUsername)
        .filter(username -> !username.equals(user.getUsername()))
        .ifPresent(username -> {
          if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
          }
          user.updateName(username);
        });

    // 이메일 수정 + 중복 체크
    Optional.ofNullable(newEmail)
        .filter(email -> !email.equals(user.getEmail()))
        .ifPresent(email -> {
          if (userRepository.existsByEmail(email)) {
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
        throw new RuntimeException("프로필 사진 업데이트 중 오류가 발생했습니다.", e);
      }
    }

    return user;
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
}
