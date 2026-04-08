package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Mock
  private UserMapper userMapper;
  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("유저 생성 성공")
  void createUser_Success() {
    // given
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "1234");

    given(userRepository.existsByName(request.getUsername())).willReturn(false);
    given(userRepository.existsByEmail(request.getEmail())).willReturn(false);

    // when
    userService.createUser(request, null);

    // then
    then(userRepository).should().save(any(User.class));
    then(userStatusRepository).should().save(any(UserStatus.class));
    then(userMapper).should().toDto(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 - 중복된 사용자 이름")
  void createUser_Fail_DuplicateUsername() {
    // given
    UserCreateRequest request = new UserCreateRequest("duplicateUser", "test@email.com", "1234");
    given(userRepository.existsByName(request.getUsername())).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.createUser(request, null))
        .isInstanceOf(DuplicateUserException.class);

    then(userRepository).should(times(0)).save(any(User.class));
  }

  @Test
  @DisplayName("유저 수정 성공")
  void updateUser_Success() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@email.com",
        "newPassword");
    User user = new User("oldUser", "old@email.com", "oldPassword", null);

    given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));
    given(userRepository.existsByName(request.getNewUsername())).willReturn(false);
    given(userRepository.existsByEmail(request.getNewEmail())).willReturn(false);

    // when
    userService.updateUser(userId, request, null);

    // then
    assertThat(user.getName()).isEqualTo(request.getNewUsername());
    assertThat(user.getEmail()).isEqualTo(request.getNewEmail());
    then(userMapper).should().toDto(user);
  }

  @Test
  @DisplayName("유저 수정 실패 - 존재하지 않는 유저")
  void updateUser_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@email.com",
        "newPassword");

    given(userRepository.findById(userId)).willReturn(java.util.Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.updateUser(userId, request, null))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should(times(0)).existsByName(anyString());
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void deleteUser_Success() {
    // given
    UUID userId = UUID.randomUUID();
    User user = new User("testUser", "test@email.com", "password", null);
    given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));

    // when
    userService.deleteUser(userId);

    // then
    then(userRepository).should().delete(user);
  }

  @Test
  @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
  void deleteUser_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(java.util.Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.deleteUser(userId))
        .isInstanceOf(UserNotFoundException.class);
    then(userRepository).should(times(0)).delete(any());
  }
}
