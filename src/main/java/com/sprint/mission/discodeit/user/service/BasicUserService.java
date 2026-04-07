package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.repository.JPABinaryContentRepository;
import com.sprint.mission.discodeit.common.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.common.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {

  private final JPAUserRepository jpaUserRepository;
  private final JPABinaryContentRepository JPABinaryContentRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final BinaryContentStorage binaryContentStorage;


  @Override
  @Transactional
  public UserDto create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    log.info("[USER_CREATE] 유저 생성 시작 : username={}, email={}", request.username(), request.email());

    if (jpaUserRepository.existsByUsername(request.username())) {
      throw new UserAlreadyExistException(Map.of("username", request.username()));
    }
    if (jpaUserRepository.existsByEmail(request.email())) {
      throw new UserAlreadyExistException(Map.of("email", request.email()));
    }

    BinaryContent profile = optionalProfileCreateRequest
        .map(profileRequest -> {
          log.info("[USER_CREATE] 프로필 요청 시작 : fileName={}, size={}",
              profileRequest.fileName(), profileRequest.bytes().length);
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          BinaryContent savedBinaryContent = JPABinaryContentRepository.save(binaryContent);
          binaryContentStorage.put(savedBinaryContent.getId(), profileRequest.bytes());
          log.info("[USER_CREATE] 프로필 요청 저장 : binaryContentID={}", savedBinaryContent.getId());
          return savedBinaryContent;
        })
        .orElse(null);

    String encodedPassword = passwordEncoder.encode(request.password());

    User user = new User(
        request.username(),
        request.email(),
        encodedPassword,
        profile
    );
    UserStatus userStatus = new UserStatus(user);
    user.setUserStatus(userStatus);

    User savedUser = jpaUserRepository.save(user);

    log.info("[USER_CREATE] 유저 생성 완료 : id={}, username={}", savedUser.getId(),
        savedUser.getUsername());

    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return jpaUserRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return jpaUserRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    log.info("[USER_UPDATE] 유저 정보 수정 시작 : userId={}, newUsername={}, newEmail={}",
        userId, request.newUsername(), request.newEmail());

    User user = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    if (jpaUserRepository.existsByUsername(request.newUsername())) {
      throw new UserAlreadyExistException(Map.of("userName", request.newUsername()));
    }
    if (jpaUserRepository.existsByEmail(request.newEmail())) {
      throw new UserAlreadyExistException(Map.of("email", request.newEmail()));
    }

    String name = Optional.ofNullable(request.newUsername()).orElse(user.getUsername());
    String email = Optional.ofNullable(request.newEmail()).orElse(user.getEmail());
    String password = Optional.ofNullable(request.newPassword())
        .map(passwordEncoder::encode)
        .orElse(user.getPassword());

    optionalProfileCreateRequest.ifPresent(profileRequest -> {
      log.debug("[USER_UPDATE] 프로필 이미지 수정 시작 : fileName={}, size={}",
          profileRequest.fileName(), profileRequest.bytes().length);
      BinaryContent newProfile = new BinaryContent(
          profileRequest.fileName(),
          (long) profileRequest.bytes().length,
          profileRequest.contentType()
      );
      BinaryContent savedBinaryContent = JPABinaryContentRepository.save(newProfile);
      binaryContentStorage.put(savedBinaryContent.getId(), profileRequest.bytes());
      user.setProfile(savedBinaryContent);
      log.debug("[USER_UPDATE] 프로필 이미지 수정 완료 : binaryContentId={}", savedBinaryContent.getId());
    });

    user.update(name, email, password);
    log.info("[USER_UPDATE] 유저 정보 수정 완료 : id={},newUserName={}, newEmail={}",
        user.getId(), request.newUsername(), request.newEmail());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    log.info("[USER_DELETE] 유저 삭제 시작 userId={}", userId);
    User user = jpaUserRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    jpaUserRepository.delete(user);
    log.info("[USER_DELETE] 유저 삭제 완료 userId={}", userId);
  }

}
