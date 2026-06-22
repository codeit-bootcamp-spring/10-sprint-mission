package com.sprint.mission.discodeit.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.support.mapper.DomainMapperContainer;
import com.sprint.mission.discodeit.user.application.mapper.UserPayloadMapper;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserRoleUpdateEvent;
import com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.domain.provider.UserNotifier;
import com.sprint.mission.discodeit.user.domain.provider.UserProfileResolver;
import com.sprint.mission.discodeit.user.domain.repository.UserRepository;
import com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository repository;
  @Mock
  private UserProfileResolver profileProvider;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private UserNotifier notifier;

  private UserPayloadMapper payloadMapper;

  private UserService userService;

  @BeforeEach
  void setUp() {
    payloadMapper = DomainMapperContainer.get(UserPayloadMapper.class);
    userService = new UserService(repository, profileProvider, eventPublisher, notifier, payloadMapper);
  }

  @Test
  @DisplayName("create - 유효한 정보를 입력하면 계정이 생성되고 이벤트를 발행한다. (Success Case)")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "pass");

    given(repository.existsByEmail("test@test.com")).willReturn(false);
    given(repository.existsByUsername("user1")).willReturn(false);

    // when
    User result = userService.create(request);

    // then
    assertThat(result.getUsername()).isEqualTo("user1");
    assertThat(result.getEmail()).isEqualTo("test@test.com");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(repository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.USER);

    verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
    verify(notifier).notifyCreated(any());
  }

  @Test
  @DisplayName("create - 이미 존재하는 이메일을 입력하면 예외가 발생한다. (Fail Case)")
  void create_fail_duplicate_email() {
    // given
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "pass");

    given(repository.existsByEmail("test@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.EMAIL_ALREADY_EXIST);
  }

  @Test
  @DisplayName("create - 이미 존재하는 유저네임을 입력하면 예외가 발생한다. (Fail Case)")
  void create_fail_duplicate_username() {
    // given
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "pass");

    given(repository.existsByEmail("test@test.com")).willReturn(false);
    given(repository.existsByUsername("user1")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.USERNAME_ALREADY_EXIST);
  }

  @Test
  @DisplayName("find - 존재하는 id로 조회하면 정상 반환한다. (Success Case)")
  void find_success() {
    // given
    UUID id = UUID.randomUUID();
    User user = User.builder().username("testuser").email("test@test.com").role(UserRole.USER)
        .build();
    ReflectionTestUtils.setField(user, "id", id);

    given(repository.findProfileById(id)).willReturn(Optional.of(user));

    // when
    User result = userService.find(id);

    // then
    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getUsername()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("find - 존재하지 않는 id로 조회하면 예외가 발생한다. (Fail Case)")
  void find_fail_notFound() {
    // given
    UUID id = UUID.randomUUID();
    given(repository.findProfileById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.find(id))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.USERID_NOT_FOUND);
  }

  @Test
  @DisplayName("findAll - 모든 유저를 조회하면 정상 반환한다.")
  void findAll_success() {
    // given
    User user1 = User.builder().username("user1").email("1@test.com").role(UserRole.USER).build();
    User user2 = User.builder().username("user2").email("2@test.com").role(UserRole.USER).build();
    given(repository.findAllUsersProfileBy()).willReturn(java.util.List.of(user1, user2));

    // when
    java.util.List<User> result = userService.findAll();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getUsername()).isEqualTo("user1");
    assertThat(result.get(1).getUsername()).isEqualTo("user2");
  }

  @Test
  @DisplayName("updateRole - 권한 변경 정보를 입력하면 권한이 변경되고 이벤트를 발행한다.")
  void updateRole_success() {
    // given
    UUID id = UUID.randomUUID();
    RoleUpdateRequest request = new RoleUpdateRequest(id, UserRole.ADMIN);
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);

    given(repository.findProfileById(id)).willReturn(Optional.of(user));

    // when
    User result = userService.updateRole(request);

    // then
    assertThat(result.getRole()).isEqualTo(UserRole.ADMIN);
    verify(eventPublisher).publishEvent(any(UserRoleUpdateEvent.class));
  }

  @Test
  @DisplayName("update - 정보 변경이 없는 경우 중복 검사를 진행하지 않는다.")
  void update_no_change_success() {
    // given
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("user1", "test@test.com", null, null);
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);

    given(repository.findProfileById(id)).willReturn(Optional.of(user));

    // when
    User result = userService.update(id, request);

    // then
    assertThat(result.getUsername()).isEqualTo("user1");
    verify(repository, org.mockito.Mockito.never()).existsByEmail(any());
    verify(repository, org.mockito.Mockito.never()).existsByUsername(any());
    verify(notifier).notifyUpdated(any());
  }

  @Test
  @DisplayName("update - 이메일 변경 시 중복 검사에서 걸리면 예외가 발생한다.")
  void update_fail_duplicate_email() {
    // given
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("user1", "new@test.com", null, null);
    User user = User.builder().username("user1").email("old@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);

    given(repository.findProfileById(id)).willReturn(Optional.of(user));
    given(repository.existsByEmail("new@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.update(id, request))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.EMAIL_ALREADY_EXIST);
  }

  @Test
  @DisplayName("update - 이름, 이메일, 비밀번호 수정 프로필 이미지 수정 등 모두 정상 변경된다.")
  void update_full_success() {
    // given
    UUID id = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newuser", "new@test.com", "newpass",
        profileId);
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);

    BinaryContent profile = BinaryContent.builder().fileName("test").contentType("image/png")
        .size(10L).build();
    ReflectionTestUtils.setField(profile, "id", profileId);

    given(repository.findProfileById(id)).willReturn(Optional.of(user));
    given(repository.existsByEmail("new@test.com")).willReturn(false);
    given(repository.existsByUsername("newuser")).willReturn(false);
    given(profileProvider.getOrThrow(profileId)).willReturn(profile);

    // when
    User result = userService.update(id, request);

    // then
    assertThat(result.getUsername()).isEqualTo("newuser");
    assertThat(result.getEmail()).isEqualTo("new@test.com");
    verify(eventPublisher).publishEvent(any(UserUpdatedEvent.class));
    verify(notifier).notifyUpdated(any());
  }

  @Test
  @DisplayName("delete - 유효한 ID를 제공하면 삭제를 수행하고 알림을 발행한다. (Success Case)")
  void delete_success() {
    // given
    UUID id = UUID.randomUUID();
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);

    given(repository.findProfileById(id)).willReturn(Optional.of(user));

    // when
    userService.delete(id);

    // then
    verify(repository).delete(user);
    verify(notifier).notifyDeleted(any());
  }

  @Test
  @DisplayName("delete - 존재하지 않는 ID를 삭제하려 하면 예외가 발생한다. (Fail Case)")
  void delete_fail_notFound() {
    // given
    UUID id = UUID.randomUUID();
    given(repository.findProfileById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.delete(id))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.USERID_NOT_FOUND);
  }
}
