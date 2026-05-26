package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private BasicUserService userService;

  private UUID userId;
  private String username;
  private String email;
  private String password;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    username = "testUser";
    email = "test@example.com";
    password = "password123";

    user = new User(username, email, password, null, UserRole.USER);
    ReflectionTestUtils.setField(user, "id", userId);
    userDto = new UserDto(userId, username, email, null, true, UserRole.USER);
  }

  @Test
  @DisplayName("사용자 생성 성공")
  void createUser_Success() {

    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(false);
    given(userRepository.existsByUsername(eq(username))).willReturn(false);
    given(passwordEncoder.encode(any())).willReturn("encodedPassword");
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    UserDto result = userService.create(request, Optional.empty());

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("이미 존재하는 이메일로 사용자 생성 시도 시 실패")
  void createUser_WithExistingEmail_ThrowsException() {

    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("이미 존재하는 사용자명으로 사용자 생성 시도 시 실패")
  void createUser_WithExistingUsername_ThrowsException() {

    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(false);
    given(userRepository.existsByUsername(eq(username))).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자 조회 성공")
  void findUser_Success() {

    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    UserDto result = userService.find(userId);

    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 조회 시 실패")
  void findUser_WithNonExistentId_ThrowsException() {

    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void updateUser_Success() {

    String newUsername = "newUsername";
    String newEmail = "new@example.com";
    String newPassword = "newPassword";
    UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);

    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(eq(newEmail))).willReturn(false);
    given(userRepository.existsByUsername(eq(newUsername))).willReturn(false);
    given(passwordEncoder.encode(any())).willReturn("encodedNewPassword");
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    UserDto result = userService.update(userId, request, Optional.empty());

    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시도 시 실패")
  void updateUser_WithNonExistentId_ThrowsException() {

    UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@example.com",
        "newPassword");
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void deleteUser_Success() {

    given(userRepository.existsById(eq(userId))).willReturn(true);

    userService.delete(userId);

    verify(userRepository).deleteById(eq(userId));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시도 시 실패")
  void deleteUser_WithNonExistentId_ThrowsException() {

    given(userRepository.existsById(eq(userId))).willReturn(false);

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
