package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j // 로깅 적용
@RequiredArgsConstructor
@Service
@Transactional // 전체 적용
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  // binaryContentRepo 삭제
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentService binaryContentService;

  @Override
  public User create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    // 생성 요청
    log.info("유저 생성 요청: username={}, email={}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.warn("유저 생성 실패: 중복 이메일 email={}", email);
      throw UserAlreadyExistsException.byEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("유저 생성 실패 - 중복 username={}", username);
      throw UserAlreadyExistsException.byUsername(username);
    }

    BinaryContent profile =
        optionalProfileCreateRequest.map(binaryContentService::create).orElse(null);

    // 각 항목둘로 생성
    User createdUser = new User(username, email, userCreateRequest.password(), profile);
    User savedUser = userRepository.save(createdUser);

    // UserStatus 는 User를 객체로 받아서 참조.
    UserStatus userStatus = new UserStatus(savedUser, Instant.now());
    userStatusRepository.save(userStatus);

    log.info("유저 생성 완료 - userId={}", savedUser.getId());

    return savedUser;
  }

  @Override
  @Transactional(readOnly = true)
  public User find(UUID userId) {
    log.debug("유저 조회 요청 - userId={}", userId);

    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> {
              log.warn("유저 조회 실패 - 존재하지 않음 userId={}", userId);
              return new UserNotFoundException(userId);
            });
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll().stream().toList();
  }

  @Override
  public User update(UUID userId, UserUpdateRequest userUpdateRequest,
                     Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    log.info("유저 수정 요청 - userId={}", userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> {
              log.warn("유저 수정 실패 - 존재하지 않음 userId={}", userId);
              return new UserNotFoundException(userId);
            });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
      log.warn("유저 수정 실패 - 중복 이메일 newEmail={}", newEmail);
      throw UserAlreadyExistsException.byEmail(newEmail);
    }

    if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
      log.warn("유저 수정 실패 - 중복 username={}", newUsername);
      throw UserAlreadyExistsException.byUsername(newUsername);
    }

    BinaryContent newProfile = optionalProfileCreateRequest
            .map(binaryContentService::create)
            .orElse(null);

    user.update(newUsername, newEmail, userUpdateRequest.newPassword(), newProfile);

    log.info("유저 수정 완료 - userId={}", userId);

    return user;
  }

  @Override
  public void delete(UUID userId) {

    log.info("유저 삭제 요청 - userId={}", userId);

    User user = userRepository.findById(userId)
            .orElseThrow(() -> {
              log.warn("유저 삭제 실패 - 존재하지 않음 userId={}", userId);
              return new UserNotFoundException(userId);
            });

    userRepository.delete(user);

    log.info("유저 삭제 완료 - userId={}", userId);
  }
}
