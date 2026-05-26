package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PasswordEncoder passwordEncoder;
  private final SessionRegistry sessionRegistry;

  @Transactional
  @Override
  public UserDto create(
      UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: {}", userCreateRequest);

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withUsername(username);
    }

    BinaryContent nullableProfile =
        optionalProfileCreateRequest
            .map(
                profileRequest -> {
                  String fileName = profileRequest.fileName();
                  String contentType = profileRequest.contentType();
                  byte[] bytes = profileRequest.bytes();
                  BinaryContent binaryContent =
                      new BinaryContent(fileName, (long) bytes.length, contentType);
                  binaryContentRepository.save(binaryContent);
                  binaryContentStorage.put(binaryContent.getId(), bytes);
                  return binaryContent;
                })
            .orElse(null);

    // 사용자가 입력한 비밀번호는 그대로 저장x
    // BCryptPasswordEncoder를 통해 해시한 값만 DB에 저장.
    String encodedPassword = passwordEncoder.encode(userCreateRequest.password());

    User user = new User(username, email, encodedPassword, nullableProfile);

    userRepository.save(user);

    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    UserDto userDto =
        userRepository
            .findById(userId)
            .map(userMapper::toDto)
            .orElseThrow(() -> UserNotFoundException.withId(userId));

    return userDto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    List<UserDto> userDtos =
        userRepository.findAllWithProfile().stream().map(userMapper::toDto).toList();

    return userDtos;
  }

  @Transactional
  @Override
  @PreAuthorize("#userId == authentication.principal.userDto.id")
  public UserDto update(
      UUID userId,
      UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> {
                  UserNotFoundException exception = UserNotFoundException.withId(userId);
                  return exception;
                });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByUsername(newUsername)) {
      throw UserAlreadyExistsException.withUsername(newUsername);
    }

    BinaryContent nullableProfile =
        optionalProfileCreateRequest
            .map(
                profileRequest -> {
                  String fileName = profileRequest.fileName();
                  String contentType = profileRequest.contentType();
                  byte[] bytes = profileRequest.bytes();
                  BinaryContent binaryContent =
                      new BinaryContent(fileName, (long) bytes.length, contentType);
                  binaryContentRepository.save(binaryContent);
                  binaryContentStorage.put(binaryContent.getId(), bytes);
                  return binaryContent;
                })
            .orElse(null);

    // 수정 시에도 인도딩된 패스워드 저장
    String newPassword = userUpdateRequest.newPassword();
    String encodedNewPassword = newPassword != null ? passwordEncoder.encode(newPassword) : null;

    user.update(newUsername, newEmail, encodedNewPassword, nullableProfile);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  @PreAuthorize("#userId == authentication.principal.userDto.id")
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    userRepository.deleteById(userId);
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user =
        userRepository
            .findById(request.userId())
            .orElseThrow(() -> UserNotFoundException.withId(request.userId()));

    user.updateRole(request.newRole());

    expireUserSessions(user.getId());

    return userMapper.toDto(user);
  }

  private void expireUserSessions(UUID userId) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(DiscodeitUserDetails.class::isInstance)
        .map(DiscodeitUserDetails.class::cast)
        .filter(principal -> principal.getUserDto().id().equals(userId))
        .forEach(
            principal ->
                sessionRegistry
                    .getAllSessions(principal, false)
                    .forEach(sessionInformation -> sessionInformation.expireNow()));
  }
}
