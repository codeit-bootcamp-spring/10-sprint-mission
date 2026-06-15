package com.sprint.mission.discodeit.user.integration;

import com.sprint.mission.discodeit.support.base.BaseIntegrationTest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.domain.exception.UserErrorCode;
import com.sprint.mission.discodeit.user.domain.exception.UserException;
import com.sprint.mission.discodeit.user.presentation.mapper.UserMapper;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import com.sprint.mission.discodeit.user.application.service.UserService;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("User 통합 테스트, 아직 덜 구현")
class UserIntegrationTest extends BaseIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  private List<User> users;

  @BeforeEach
  void setUp() {
    User user = User.builder()
        .username("integrationuser")
        .email("integration@test.com")
        .role(UserRole.USER)
        .build();
    em.persist(user);
    em.flush();
    users = List.of(user);
  }

  @Test
  @DisplayName("success to find user by id")
  void success_to_find() {
    User user = users.get(0);
    UserResponse expected = userService.find(user.getId());
    Assertions.assertThat(expected)
        .extracting("id", "username", "email")
        .containsExactly(user.getId(), user.getUsername(), user.getEmail());
  }

  @Test
  @DisplayName("fail to find user by id")
  void fail_to_find() {
    UUID userId = UUID.randomUUID();
    Assertions.assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("success to create user")
  void success_to_create() {
    UserCreateRequest request = new UserCreateRequest("newuser", "newuser@email.com", "password");
    UserResponse expected = userService.create(request);
    flushAndClear();
    User actual = userRepository.findById(expected.id()).orElse(null);
    Assertions.assertThat(actual.getUsername()).isEqualTo(expected.username());
  }

  @Test
  @DisplayName("fail to create user due to existing username")
  void fail_to_create_user_with_existing_username() {
    String username = users.get(0).getUsername();
    String password = "bbqwed";
    String email = "dncie@ciom.com";
    UserCreateRequest request = new UserCreateRequest(username, email, password);

    Assertions.assertThatThrownBy(() -> {
          userService.create(request);
          em.flush();
        })
        .isInstanceOf(UserException.class)
        .hasMessage(UserErrorCode.USERNAME_ALREADY_EXIST.getMessage());
  }

  @Test
  @DisplayName("fail to create user due to existing email")
  void fail_to_create_user_with_existing_email() {
    String username = "leee";
    String email = users.get(0).getEmail();
    String password = ",cki4e3d";
    UserCreateRequest request = new UserCreateRequest(username, email, password);

    Assertions.assertThatThrownBy(() -> {
          userService.create(request);
          em.flush();
        })
        .isInstanceOf(UserException.class)
        .hasMessage(UserErrorCode.EMAIL_ALREADY_EXIST.getMessage());
  }

  @Test
  @DisplayName("success to update user")
  void success_to_update() {
    User user = users.get(0);
    UUID userId = user.getId();
    UserUpdateRequest request = new UserUpdateRequest("updatedUsername", "updated@email.com",
        "password", null);
    UserResponse updated = userService.update(userId, request);
    flushAndClear();
    Assertions.assertThat(updated.username()).isEqualTo(request.getUsername());
  }

  @Test
  @DisplayName("fail to update user due to incorrect id")
  void fail_to_update() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("updatedUsername", "updated@email.com",
        "password", null);
    Assertions.assertThatThrownBy(() -> {
          userService.update(userId, request);
          em.flush();
        })
        .isInstanceOf(UserException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("success to delete")
  void success_to_delete() {
    UUID userId = users.get(0).getId();
    userService.delete(userId);
    flushAndClear();
    Assertions.assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("fail to delete since deleted user")
  void fail_to_delete() {
    UUID userId = users.get(0).getId();
    userService.delete(userId);
    flushAndClear();
    Assertions.assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }
}
