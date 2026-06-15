package com.sprint.mission.discodeit.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.support.mapper.MapperContainer;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.event.UserCreatedEvent;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.domain.provider.UserProfileResolver;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.presentation.mapper.UserMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("UserService Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserProfileResolver profileProvider;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Spy
  private UserMapper userMapper = MapperContainer.get(UserMapper.class);

  @InjectMocks
  private UserService userService;

  @Test
  @DisplayName("create - 유효한 정보를 입력하면 사용자가 생성되고 이벤트가 발행된다. (Success Case)")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "pass");

    given(userRepository.existsByEmail("test@test.com")).willReturn(false);
    given(userRepository.existsByUsername("user1")).willReturn(false);

    // when
    UserResponse result = userService.create(request);

    // then
    assertThat(result.username()).isEqualTo("user1");
    assertThat(result.email()).isEqualTo("test@test.com");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getRole()).isEqualTo(UserRole.USER);

    verify(eventPublisher).publishEvent(any(UserCreatedEvent.class));
  }

  @Test
  @DisplayName("create - 이미 존재하는 이메일을 입력하면 예외가 발생한다. (Fail Case)")
  void create_fail_duplicate_email() {
    // given
    UserCreateRequest request = new UserCreateRequest("user1", "test@test.com", "pass");

    given(userRepository.existsByEmail("test@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.EMAIL_ALREADY_EXIST);
  }

  @Test
  @DisplayName("find - 존재하는 id를 조회하면 정상 반환된다. (Success Case)")
  void find_success() {
    // given
    UUID id = UUID.randomUUID();
    User user = User.builder().username("testuser").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);

    given(userRepository.findProfileById(id)).willReturn(Optional.of(user));

    // when
    UserResponse result = userService.find(id);

    // then
    assertThat(result.id()).isEqualTo(id);
    assertThat(result.username()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("find - 존재하지 않는 id를 조회하면 예외가 발생한다. (Fail Case)")
  void find_fail_notFound() {
    // given
    UUID id = UUID.randomUUID();
    given(userRepository.findProfileById(id)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.find(id))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.USERID_NOT_FOUND);
  }

  @Test
  @DisplayName("findAll - 모든 유저를 조회하면 정상 반환된다.")
  void findAll_success() {
    // given
    User user1 = User.builder().username("user1").email("1@test.com").role(UserRole.USER).build();
    User user2 = User.builder().username("user2").email("2@test.com").role(UserRole.USER).build();
    given(userRepository.findAllUsersProfileBy()).willReturn(java.util.List.of(user1, user2));

    // when
    java.util.List<UserResponse> result = userService.findAll();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).username()).isEqualTo("user1");
    assertThat(result.get(1).username()).isEqualTo("user2");
  }

  @Test
  @DisplayName("updateRole - 역할 수정 시 변경 내용이 저장되고 이벤트가 발행된다.")
  void updateRole_success() {
    // given
    UUID id = UUID.randomUUID();
    com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest request = new com.sprint.mission.discodeit.user.presentation.dto.request.RoleUpdateRequest(id, UserRole.ADMIN);
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);
    
    given(userRepository.findProfileById(id)).willReturn(Optional.of(user));

    // when
    UserResponse result = userService.updateRole(request);

    // then
    assertThat(result.role()).isEqualTo(UserRole.ADMIN);
    verify(eventPublisher).publishEvent(any(com.sprint.mission.discodeit.user.domain.event.UserRoleUpdateEvent.class));
  }

  @Test
  @DisplayName("update - 정보 변경이 없는 경우 중복 검사를 수행하지 않는다.")
  void update_no_change_success() {
    // given
    UUID id = UUID.randomUUID();
    com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest request = new com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest("user1", "test@test.com", null, null);
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);
    
    given(userRepository.findById(id)).willReturn(Optional.of(user));

    // when
    UserResponse result = userService.update(id, request);

    // then
    assertThat(result.username()).isEqualTo("user1");
    verify(userRepository, org.mockito.Mockito.never()).existsByEmail(any());
    verify(userRepository, org.mockito.Mockito.never()).existsByUsername(any());
  }

  @Test
  @DisplayName("update - 이메일 변경 시 중복 검사에서 걸리면 예외가 발생한다.")
  void update_fail_duplicate_email() {
    // given
    UUID id = UUID.randomUUID();
    com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest request = new com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest("user1", "new@test.com", null, null);
    User user = User.builder().username("user1").email("old@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);
    
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.update(id, request))
        .isInstanceOf(UserException.class)
        .extracting("errorCode")
        .isEqualTo(UserErrorCode.EMAIL_ALREADY_EXIST);
  }

  @Test
  @DisplayName("update - 이름, 이메일, 비밀번호 수정 및 프로필 이미지 수정 시 모두 정상 변경된다.")
  void update_full_success() {
    // given
    UUID id = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();
    com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest request = new com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest("newuser", "new@test.com", "newpass", profileId);
    User user = User.builder().username("user1").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(user, "id", id);
    
    com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent profile = com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent.builder().fileName("test").contentType("image/png").size(10L).build();
    ReflectionTestUtils.setField(profile, "id", profileId);
    
    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@test.com")).willReturn(false);
    given(userRepository.existsByUsername("newuser")).willReturn(false);
    given(profileProvider.getProxyOrThrow(profileId)).willReturn(profile);

    // when
    UserResponse result = userService.update(id, request);

    // then
    assertThat(result.username()).isEqualTo("newuser");
    assertThat(result.email()).isEqualTo("new@test.com");
    verify(eventPublisher).publishEvent(any(com.sprint.mission.discodeit.user.domain.event.UserUpdatedEvent.class));
  }
}
