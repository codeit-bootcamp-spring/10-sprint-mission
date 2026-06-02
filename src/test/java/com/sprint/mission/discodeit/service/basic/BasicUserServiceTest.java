package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.user.UserEmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
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
  private ReadStatusRepository readStatusRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentMapper binaryContentMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtRegistry jwtRegistry;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자는 정상적으로 등록이 되어야 합니다.")
  void create_success() {
    // given
    User user = new User("김러키", "lucky@google.com", "123asd");
    UUID userId = UUID.randomUUID();
    ReflectionTestUtils.setField(user, "id", userId);

    UserCreateRequest request = new UserCreateRequest(
        "김러키",
        "lucky@google.com",
        "123asd",
        null
    );

    UserResponse response = new UserResponse(
        userId,
        user.getUsername(),
        user.getEmail(),
        false,
        null,
        null,
        user.getRole()
    );

    given(userRepository.existsByUsername(request.userName())).willReturn(false);
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(passwordEncoder.encode(request.password())).willReturn("encoded-password");
    given(userRepository.save(any(User.class))).willReturn(user);
    given(jwtRegistry.hasActiveJwtInformationByUserId(any(UUID.class))).willReturn(false);
    given(userMapper.toResponse(any(User.class), anyBoolean(), any())).willReturn(response);

    // when
    UserResponse result = userService.create(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.userName()).isEqualTo(request.userName());
    assertThat(result.email()).isEqualTo(request.email());
    assertThat(result.role()).isEqualTo(UserRole.USER);

    then(userRepository).should().existsByUsername(request.userName());
    then(userRepository).should().existsByEmail(request.email());
    then(passwordEncoder).should().encode(request.password());
    then(userRepository).should().save(any(User.class));
    then(jwtRegistry).should().hasActiveJwtInformationByUserId(userId);
  }

  @Test
  @DisplayName("이미 존재하는 이메일이 들어오면 예외가 발생해야 합니다.")
  void create_fail_duplicate_email() {
    // given
    UserCreateRequest request = new UserCreateRequest(
        "김러키",
        "lucky@google.com",
        "123asd",
        null
    );

    given(userRepository.existsByUsername(request.userName())).willReturn(false);
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    // when, then
    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(UserEmailAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자의 정보는 정상적으로 수정되어야 합니다.")
  void update_success() {
    // given
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");

    User user = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(user, "id", userId);

    UserUpdateRequest request = new UserUpdateRequest(
        userId,
        Optional.of("김러키_수정"),
        Optional.of("lucky_fix@google.com"),
        Optional.empty(),
        Optional.empty()
    );

    UserResponse response = new UserResponse(
        userId,
        "김러키_수정",
        "lucky_fix@google.com",
        false,
        null,
        null,
        user.getRole()
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.save(any(User.class))).willReturn(user);
    given(jwtRegistry.hasActiveJwtInformationByUserId(any(UUID.class))).willReturn(false);
    given(userMapper.toResponse(any(User.class), anyBoolean(), any())).willReturn(response);

    // when
    UserResponse result = userService.update(request);

    // then
    assertThat(result.userName()).isEqualTo("김러키_수정");
    assertThat(result.email()).isEqualTo("lucky_fix@google.com");

    then(userRepository).should().findById(userId);
    then(userRepository).should().save(any(User.class));
    then(jwtRegistry).should().hasActiveJwtInformationByUserId(userId);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시 예외가 발생해야 합니다.")
  void update_fail_not_found() {
    // given
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");

    UserUpdateRequest request = new UserUpdateRequest(
        userId,
        Optional.of("김러키_수정"),
        Optional.of("lucky_fix@google.com"),
        Optional.empty(),
        Optional.empty()
    );

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> userService.update(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 권한은 정상적으로 수정되어야 합니다.")
  void update_role_success() {
    // given
    UUID userId = UUID.randomUUID();

    User user = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(user, "id", userId);

    UserRoleUpdateRequest request =
        new UserRoleUpdateRequest(userId, UserRole.CHANNEL_MANAGER);

    UserResponse response = new UserResponse(
        userId,
        user.getUsername(),
        user.getEmail(),
        false,
        null,
        null,
        UserRole.CHANNEL_MANAGER
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.save(any(User.class))).willReturn(user);
    willDoNothing().given(jwtRegistry).invalidateJwtInformationByUserId(userId);
    given(jwtRegistry.hasActiveJwtInformationByUserId(any(UUID.class))).willReturn(false);
    given(userMapper.toResponse(any(User.class), anyBoolean(), any())).willReturn(response);

    // when
    UserResponse result = userService.updateRole(request);

    // then
    assertThat(result.role()).isEqualTo(UserRole.CHANNEL_MANAGER);

    then(userRepository).should().findById(userId);
    then(userRepository).should().save(any(User.class));
    then(jwtRegistry).should().invalidateJwtInformationByUserId(userId);
    then(jwtRegistry).should().hasActiveJwtInformationByUserId(userId);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 권한 수정 시 예외가 발생해야 합니다.")
  void update_role_fail_not_found() {
    // given
    UUID userId = UUID.randomUUID();

    UserRoleUpdateRequest request =
        new UserRoleUpdateRequest(userId, UserRole.CHANNEL_MANAGER);

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> userService.updateRole(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자는 정상적으로 삭제되어야 합니다.")
  void delete_success() {
    // given
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");

    User user = new User("김러키", "lucky@google.com", "123asd");
    ReflectionTestUtils.setField(user, "id", userId);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    willDoNothing().given(readStatusRepository).deleteByUserId(userId);

    // when
    userService.delete(userId);

    // then
    then(userRepository).should().findById(userId);
    then(readStatusRepository).should().deleteByUserId(userId);
    then(userRepository).should().delete(user);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시 예외가 발생해야 합니다.")
  void delete_fail_not_found() {
    // given
    UUID userId = UUID.fromString("c382b0ae-7c18-4159-8a0a-8fb7d7589ddf");

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}