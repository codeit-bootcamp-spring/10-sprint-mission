package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.IntegratedTestSupport;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.user.UserErrorCode;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

// notice: UserService + UserRepository 통합 테스트
@Disabled("Service + Repository + controller 통합 테스트를 위해 잠시 보류")
class BasicUserServiceTest extends IntegratedTestSupport {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private UserMapper userMapper;

  private List<User> users = new ArrayList<>();

  @BeforeEach
  void setUp() {
    users.clear();
    for (int i = 0; i < 10; i++) {
      UserCreateRequest request = UserFixture.createRequest();
      UserStatus status = UserStatusFixture.createOnline();
      Optional<BinaryContent> profile = Optional.of(BinaryContentFixture.createEntity());
      User user = userMapper.toEntityFrom(request, status, profile);
      users.add(user);
    }
    userRepository.saveAllAndFlush(users);
  }

  @Test
  @DisplayName("success to find user by id")
  void success_to_find() {
    User user = users.get(0);
    UserDto expected = userService.find(user.getId());
    Assertions.assertThat(expected)
        .extracting("id", "username", "email")
        .containsExactly(user.getId(), user.getUsername(), user.getEmail());
  }

  @Test
  @DisplayName("fail to find user by id")
  void fail_to_find() {
    UUID userId = UUID.randomUUID();
    Assertions.assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("success to find all user")
  void success_to_findAll() {
    List<UserDto> expected = userService.findAll();
    for (int i = 0; i < 10; i++) {
      Assertions.assertThat(expected.get(i))
          .usingRecursiveComparison()
          .isEqualTo(userMapper.toDto(users.get(i)));
    }
  }

  @Test
  @DisplayName("success to create user")
  void success_to_create() {
    UserCreateRequest request = UserFixture.createRequest();
    MultipartFile file = BinaryContentFixture.createFile();
    Optional<FileUploadDto> fileDto = FileUploadDto.from(file);
    UserDto expected = userService.create(request, fileDto);
    flushAndClear();
    User actual = userRepository.findById(expected.id()).orElse(null);
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("fail to create user due to existing username")
  void fail_to_create_user_with_existing_username() {
    String username = users.get(0).getUsername();
    String password = "bbqwed";
    String email = "dncie@ciom.com";
    MultipartFile file = BinaryContentFixture.createFile();
    Optional<FileUploadDto> fileDto = FileUploadDto.from(file);
    UserCreateRequest request = new UserCreateRequest(username, email, password);

    Assertions.assertThatThrownBy(() -> {
          userService.create(request, fileDto);
          em.flush();
        })
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.USERNAME_ALREADY_EXIST.getMessage());
  }

  @Test
  @DisplayName("fail to create user due to existing email")
  void fail_to_create_user_with_existing_email() {
    String username = "leee";
    String email = users.get(1).getEmail();
    String password = ",cki4e3d";
    MultipartFile file = BinaryContentFixture.createFile();
    Optional<FileUploadDto> fileDto = FileUploadDto.from(file);
    UserCreateRequest request = new UserCreateRequest(username, email, password);

    Assertions.assertThatThrownBy(() -> {
          userService.create(request, fileDto);
          em.flush();
        })
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.EMAIL_ALREADY_EXIST.getMessage());
  }

  @Test
  @DisplayName("success to update user")
  void success_to_update() {
    User user = users.get(0);
    UUID userId = user.getId();
    UUID originProfileId = user.getProfile().getId();
    UserUpdateRequest request = UserFixture.createUpdate();
    MultipartFile file = BinaryContentFixture.createFile();
    Optional<FileUploadDto> fileDto = FileUploadDto.from(file);
    UserDto updated = userService.update(userId, request, fileDto);
    flushAndClear();
    Assertions.assertThat(updated)
        .extracting("id", "username", "email", "profile.fileName", "profile.size",
            "profile.contentType")
        .containsExactly(userId, request.getUsername(), request.getEmail(), file.getName(),
            file.getSize(), file.getContentType());
    Assertions.assertThat(updated.profile().id())
        .isNotNull()
        .isNotEqualTo(originProfileId);
  }

  @Test
  @DisplayName("fail to update user due to incorrect id")
  void fail_to_update() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = UserFixture.createUpdate();
    Assertions.assertThatThrownBy(() -> {
          userService.update(userId, request, Optional.empty());
          em.flush();
        })
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("success to update partially, where username, password, profile image")
  void success_to_partial_update() {
    User originUser = users.get(0);
    UUID userId = originUser.getId();
    UUID originProfileId = originUser.getProfile().getId();
    UserUpdateRequest request = new UserUpdateRequest("leee", null, "leee1234");
    MultipartFile file = BinaryContentFixture.createFile();
    Optional<FileUploadDto> fileDto = FileUploadDto.from(file);
    UserDto expected = userService.update(userId, request, fileDto);
    flushAndClear();

    Assertions.assertThat(expected)
        .extracting("id", "username", "email")
        .containsExactly(userId, "leee", originUser.getEmail());

    Assertions.assertThat(expected.profile())
        .returns(file.getName(), Assertions.from(BinaryContentDto::fileName))
        .returns(file.getSize(), Assertions.from(BinaryContentDto::size))
        .returns(file.getContentType(), Assertions.from(BinaryContentDto::contentType));
    Assertions.assertThat(expected.profile().id()).isNotEqualTo(originProfileId);
  }

  @Test
  @DisplayName("fail to update partially due to existing username")
  void fail_to_partial_update_with_existing_username() {
    User user = users.get(0);
    UUID userId = user.getId();
    String existingUsername = users.get(1).getUsername();
    UserUpdateRequest request
        = new UserUpdateRequest(existingUsername, null, null);
    Assertions.assertThatThrownBy(() -> {
          userService.update(userId, request, Optional.empty());
          flushAndClear();
        })
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.USERNAME_ALREADY_EXIST.getMessage());
    Assertions.assertThat(user.getEmail()).isNotEmpty();
    Assertions.assertThat(user.getProfile()).isNotNull();
  }

  @Test
  @DisplayName("fail to update partially due to existing email")
  void fail_to_partial_update_with_existing_email() {
    User user = users.get(2);
    UUID userId = user.getId();
    String existingEmail = users.get(3).getEmail();
    UserUpdateRequest request = new UserUpdateRequest(null, existingEmail, null);

    Assertions.assertThatThrownBy(() -> {
          userService.update(userId, request, Optional.empty());
          flushAndClear();
        })
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.EMAIL_ALREADY_EXIST.getMessage());
    Assertions.assertThat(user.getUsername()).isNotEmpty();
    Assertions.assertThat(user.getProfile()).isNotNull();
  }

  @Test
  @DisplayName("success to delete")
  void success_to_delete() {
    UUID userId = users.get(0).getId();
    userService.delete(userId);
    flushAndClear();
    Assertions.assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("fail to delete since deleted user")
  void fail_to_delete() {
    UUID userId = users.get(0).getId();
    userService.delete(userId);
    flushAndClear();
    Assertions.assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(DiscodeitException.class)
        .hasMessage(UserErrorCode.USERID_NOT_FOUND.getMessage());
  }
}
