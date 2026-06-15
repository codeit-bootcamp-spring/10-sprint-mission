package com.sprint.mission.discodeit.user.application.service;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserRoleUpdateEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.domain.provider.UserProfileResolver;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.presentation.mapper.UserMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository repository;
  private final UserMapper mapper;
  private final UserProfileResolver profileProvider;
  private final ApplicationEventPublisher eventPublisher;

  @ServiceLogAround
  @Transactional(readOnly = true)
  public UserResponse find(UUID id) {
    User user = findById(id);
    return mapper.toDto(user);
  }

  @Transactional(readOnly = true)
  public List<UserResponse> findAll() {
    List<User> users = repository.findAllUsersProfileBy();
    return mapper.toDto(users);
  }

  @ServiceLogAround
  public UserResponse create(UserCreateRequest request) {
    checkEmailUniqueness(request.getEmail());
    checkUsernameUniqueness(request.getUsername());

    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .role(UserRole.USER)
        .build();
    repository.save(user);
    eventPublisher.publishEvent(new UserCreatedEvent(user.getId(), request.getPassword()));
    return mapper.toDto(user);
  }

  @ServiceLogAround
  public UserResponse update(UUID id, UserUpdateRequest request) {
    User user = findByIdWithLazy(id);
    if (user.updateEmail(request.getEmail())) {
      checkEmailUniqueness(request.getEmail());
    }
    if (user.updateUsername(request.getUsername())) {
      checkUsernameUniqueness(request.getUsername());
    }
    if (request.getProfileId() != null) {
      BinaryContent profile = profileProvider.getProxyOrThrow(request.getProfileId());
      user.updateProfile(profile);
    }
    if (StringUtils.hasText(request.getPassword())) {
      eventPublisher.publishEvent(new UserUpdatedEvent(id, request.getPassword()));
    }
    return mapper.toDto(user);
  }

  @ServiceLogAround
  public void delete(UUID id) {
    DomainServiceSupport.deleteOrThrow(id, repository,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  @ServiceLogAround
  public UserResponse updateRole(RoleUpdateRequest request) {
    UUID id = request.getId();
    User user = findById(id);
    UserRole oldRole = user.getRole();
    UserRole newRole = request.getRole();
    user.updateRole(newRole);
    eventPublisher.publishEvent(new UserRoleUpdateEvent(id, oldRole, newRole));
    return mapper.toDto(user);
  }

  private User findById(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findProfileById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  private User findByIdWithLazy(UUID id) {
    return DomainServiceSupport.getOrThrow(id, repository::findById,
        value -> new UserException(UserErrorCode.USERID_NOT_FOUND, value));
  }

  private void checkUsernameUniqueness(String username) {
    DomainServiceSupport.requireOrThrow(username, Predicate.not(repository::existsByUsername),
        value -> new UserException(UserErrorCode.USERNAME_ALREADY_EXIST,
            Map.of("username", value)));
  }

  private void checkEmailUniqueness(String email) {
    DomainServiceSupport.requireOrThrow(email, Predicate.not(repository::existsByEmail),
        value -> new UserException(UserErrorCode.EMAIL_ALREADY_EXIST,
            Map.of("email", value)));
  }
}
