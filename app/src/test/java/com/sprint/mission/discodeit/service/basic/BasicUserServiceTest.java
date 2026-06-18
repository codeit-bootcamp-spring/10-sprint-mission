package com.sprint.mission.discodeit.service.basic;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  BinaryContentRepository binaryContentRepository;
  @Mock
  BinaryContentStorage binaryContentStorage;
  @Mock
  UserMapper mapper;

  @InjectMocks
  BasicUserService userService;

  @Nested
  class createUser {

    @Test
    @DisplayName("유저이름,패스워드,이메일만으로 userDto 반환")
    void should_return_userDto_when_username_and_password_email_without_profile()
        throws IOException {
      // given
      String username = "A";
      String email = "A@gmail.com";
      String password = "AAA";
      User user = new User(username, password, email);
      UserDto.UserCreateRequest request = new UserCreateRequest(username, password, email);

      given(userRepository.existsByUsername(anyString())).willReturn(false);
      given(userRepository.existsByEmail(anyString())).willReturn(false);
      given(userRepository.save(any(User.class))).willReturn(user);
      UserDto expectedDto = new UserDto(UUID.randomUUID(), username, email, null, true);
      given(mapper.toDto(any(User.class))).willReturn(expectedDto);

      // when
      UserDto actualDto = userService.createUser(request, null);

      // then
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      then(userRepository).should().save(userCaptor.capture());
      User getUser = userCaptor.getValue();

      assertNotNull(getUser.getStatus());
      assertNull(getUser.getProfile());

      assertEquals(expectedDto.username(), actualDto.username());
      assertEquals(expectedDto.email(), actualDto.email());
    }

    @Test
    @DisplayName("유저이름,패스워드,이메일과 프로필 이미지로 userDto 반환")
    void should_return_userDto_when_username_and_password_and_email_with_profile()
        throws IOException {
      // given
      String username = "A";
      String email = "A@gmail.com";
      String password = "AAA";
      User user = new User(username, password, email);
      UserDto.UserCreateRequest request = new UserCreateRequest(username, password, email);

      given(userRepository.existsByUsername(anyString())).willReturn(false);
      given(userRepository.existsByEmail(anyString())).willReturn(false);
      given(userRepository.save(any(User.class))).willReturn(user);
      UserDto expectedDto = new UserDto(UUID.randomUUID(), username, email, null, true);
      given(mapper.toDto(any(User.class))).willReturn(expectedDto);

      MockMultipartFile mockFile = new MockMultipartFile(
          "test", "test_file_name", "test_content_Type", "test".getBytes());

      // when
      userService.createUser(request, mockFile);

      // then
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      then(userRepository).should().save(userCaptor.capture());
      User getUser = userCaptor.getValue();

      assertNotNull(getUser.getStatus());
      assertNotNull(getUser.getProfile());

      then(binaryContentRepository).should().save(any(BinaryContent.class));
      then(binaryContentStorage).should().put(any(), any(byte[].class));  // jpa라 단위테스트에선 null임
    }

    @Test
    @DisplayName("중복된 이름이 이미 있으면 에러 반환")
    void should_throw_exception_when_duplicate_username() {
      // given
      given(userRepository.existsByUsername(anyString())).willReturn(true);

      // when, then
      assertThrows(DuplicateUsernameException.class,
          () -> userService.createUser(new UserCreateRequest("A", "AAA", "A@gmail.com"), null));
    }

    @Test
    @DisplayName("중복된 메일주소가 이미 있으면 에러 반환")
    void should_throw_exception_when_duplicate_email() {
      // given
      given(userRepository.existsByEmail(anyString())).willReturn(true);

      // when, then
      assertThrows(DuplicateEmailException.class,
          () -> userService.createUser(new UserCreateRequest("A", "AAA", "A@gmail.com"), null));
    }
  }

  @Nested
  class updateUser {

    @Test
    @DisplayName("새로운 필드를 가진 userDto 반환")
    void should_return_userDto_when_new_fields_without_profile() throws IOException {
      // given
      String newUsername = "G";
      String newEmail = "G@outlook.com";
      User existingUser = new User("A", "AA", "A@yahoo.com");
      UserUpdateRequest request = new UserUpdateRequest(newUsername, "G1", newEmail);
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
      UserDto expectedDto = new UserDto(UUID.randomUUID(), newUsername, newEmail, null, true);
      given(mapper.toDto(any(User.class))).willReturn(expectedDto);

      // when
      UserDto actualDto = userService.updateUser(UUID.randomUUID(), request, null);

      // then
      assertEquals(request.newUsername(), actualDto.username());
      assertEquals(request.newEmail(), actualDto.email());
    }

    @Test
    @DisplayName("값이 바뀌지 않은 채 userDto 반환")
    void should_return_userDto_when_null_fields_without_profile() throws IOException {
      // given
      User existingUser = new User("A", "AA", "A@yahoo.com");
      UserUpdateRequest request = new UserUpdateRequest(null, null, null);
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
      UserDto expectedDto = new UserDto(UUID.randomUUID(), existingUser.getUsername(),
          existingUser.getEmail(), null, true);
      given(mapper.toDto(any(User.class))).willReturn(expectedDto);

      // when
      UserDto actualDto = userService.updateUser(UUID.randomUUID(), request, null);

      // then
      assertEquals(existingUser.getUsername(), actualDto.username());
      assertEquals(existingUser.getEmail(), actualDto.email());
    }

    @Test
    @DisplayName("프로필 이미지만 수정된 채 userDto 반환")
    void should_return_userDto_when_null_fields_with_profile() throws IOException {
      // given
      User existingUser = new User("A", "AA", "A@yahoo.com");
      UserUpdateRequest request = new UserUpdateRequest(null, null, null);
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
      UserDto expectedDto = new UserDto(UUID.randomUUID(), existingUser.getUsername(),
          existingUser.getEmail(), null, true);
      given(mapper.toDto(any(User.class))).willReturn(expectedDto);

      MockMultipartFile mockFile = new MockMultipartFile(
          "test", "test_file_name", "test_content_Type", "test".getBytes());

      // when
      userService.updateUser(UUID.randomUUID(), request, mockFile);

      // then
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      then(userRepository).should().save(userCaptor.capture());
      User getUser = userCaptor.getValue();

      assertNotNull(getUser.getProfile());

      then(binaryContentRepository).should().save(any(BinaryContent.class));
      then(binaryContentStorage).should().put(any(), any(byte[].class));
    }

    @Test
    @DisplayName("존재하지 않는 유저가 있을 경우 에러 반환")
    void should_throw_exception_when_missing_userId() {
      // given
      UserUpdateRequest request = new UserUpdateRequest("A", "AA", "AAA@gmail.com");
      given(userRepository.findById(any(UUID.class))).willThrow(UserNotFoundException.class);

      // when, then
      assertThrows(UserNotFoundException.class, () ->
          userService.updateUser(UUID.randomUUID(), request, null));
    }

    @Test
    @DisplayName("중복된 이름이 이미 있으면 에러 반환")
    void should_throw_exception_when_duplicate_name() {
      // given
      User existingUser = new User("A", "AA", "A@yahoo.com");
      UserUpdateRequest request = new UserUpdateRequest("G", null, null);
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
      given(userRepository.existsByUsername(anyString())).willReturn(true);

      // when, then
      assertThrows(DuplicateUsernameException.class,
          () -> userService.updateUser(UUID.randomUUID(), request, null));
    }

    @Test
    @DisplayName("중복된 메일주소가 이미 있으면 에러 반환")
    void should_throw_exception_when_duplicate_email() {
      // given
      User existingUser = new User("A", "AA", "A@yahoo.com");
      UserUpdateRequest request = new UserUpdateRequest(null, null, "G@outlook.com");
      given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
      given(userRepository.existsByEmail(anyString())).willReturn(true);

      // when, then
      assertThrows(DuplicateEmailException.class,
          () -> userService.updateUser(UUID.randomUUID(), request, null));
    }
  }

  @Nested
  class deleteUser {

    @Test
    @DisplayName("유저가 존재할 경우 성공적으로 삭제")
    void should_delete_user_when_id_exists() {
      // given
      UUID id = UUID.randomUUID();
      given(userRepository.existsById(id)).willReturn(true);

      // when
      userService.deleteUser(id);

      // then
      then(userRepository).should(atLeastOnce()).deleteById(id);
    }

    @Test
    @DisplayName("유저가 존재하지 않을 경우 에러 반환")
    void should_throw_exception_when_user_not_found() {
      // given
      given(userRepository.existsById(any(UUID.class))).willThrow(UserNotFoundException.class);

      // when, then
      assertThrows(UserNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
      then(userRepository).should(never()).deleteById(any(UUID.class));
    }
  }
}