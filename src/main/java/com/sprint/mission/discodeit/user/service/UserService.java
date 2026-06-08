package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.auth.repository.UserCredentialRepository;
import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.exception.BinaryContentException;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.exception.UserException;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final UserCredentialRepository userCredentialRepository;
  private final BinaryContentRepository binaryContentRepository;

  @ServiceLogAround
  @Transactional(readOnly = true)
  public UserResponse find(UUID id) {
    User user = findById(id);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> findAll() {
    List<User> users = userRepository.findAllUsersProfileBy();
    return userMapper.toDto(users);
  }

  @ServiceLogAround
  public UserResponse create(UserCreateRequest request) {
    checkEmailUniqueness(request.getEmail());
    checkUsernameUniqueness(request.getUsername());

    BinaryContent binaryContent = getProfileOrNot(request.getProfileId());
    User user = userMapper.toEntityFrom(request, binaryContent);
    userRepository.save(user);
    createUserCredential(request, user);
    return userMapper.toDto(user);
  }

  /*
   * todo: Regarding profile image updates.
   *  If an image already exists and the received image is null,
   *  distinguish whether to delete the image or not update it.
   * */
  @ServiceLogAround
  public UserResponse update(UUID id, UserUpdateRequest request) {
    checkEmailUniqueness(request.getEmail());
    checkUsernameUniqueness(request.getUsername());

    User user = findById(id);
    BinaryContent binaryContent = getProfileOrNot(request.getProfileId());
    userMapper.partialUpdate(request, binaryContent, user);
    updateUserCredential(user.getEmail(), request.getPassword());
    return userMapper.toDto(user);
  }

  @ServiceLogAround
  public void delete(UUID id) {
    DomainServiceSupport.executeOrThrow(id, userRepository,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  private User findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, userRepository::findProfileById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  private void checkUsernameUniqueness(String username) {
    if (username == null) {
      return;
    }
    DomainServiceSupport.requireOrThrow(username, Predicate.not(userRepository::existsByUsername),
        value -> new UserException(UserErrorCode.USERNAME_ALREADY_EXIST,
            Map.of("username", value)));
  }

  private void checkEmailUniqueness(String email) {
    if (email == null) {
      return;
    }
    DomainServiceSupport.requireOrThrow(email, Predicate.not(userRepository::existsByEmail),
        value -> new UserException(UserErrorCode.EMAIL_ALREADY_EXIST,
            Map.of("email", value)));
  }

  private void updateUserCredential(String email, String rawPassword) {
    if (rawPassword == null) {
      return;
    }
    UserCredential userCredential = userCredentialRepository.findByUser_Email(email).orElseThrow();
    userCredential.setPassword(passwordEncoder.encode(rawPassword));
  }

  private void createUserCredential(UserCreateRequest request, User user) {
    UserCredential userCredential = new UserCredential(user,
        passwordEncoder.encode(request.getPassword()));
    userCredentialRepository.save(userCredential);
  }

  private BinaryContent getProfileOrNot(UUID profileId) {
    if (profileId == null) {
      return null;
    }
    DomainServiceSupport.requireOrThrow(profileId, binaryContentRepository::existsById,
        value -> new BinaryContentException(BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND,
            value));
    return binaryContentRepository.getReferenceById(profileId);
  }
}
