package com.sprint.mission.discodeit.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.common.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.common.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserDto;
import com.sprint.mission.discodeit.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.JPAUserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  JPAUserRepository jpaUserRepository;
  @Mock
  PasswordEncoder passwordEncoder;
  @Mock
  UserMapper userMapper;

  @InjectMocks
  BasicUserService basicUserService;


  private UserDto mockDto;
  private User mockUser;

  @BeforeEach
  void setUp() {
    mockDto = new UserDto(UUID.randomUUID(), Instant.now(), Instant.now(), "이름", "이메일", null,
        false);
    mockUser = new User("testUser", "test@test.com", "encodedPassword", null);
  }


  @Nested
  @DisplayName("유저 생성 테스트")
  class UserCreate {

    private UserCreateRequest userCreateRequest;

    @BeforeEach
    void setUp() {
      userCreateRequest = new UserCreateRequest("testUser", "test@test.com", "password1234");
    }

    @Test
    @DisplayName("유저 생성 테스트 성공")
    void UserCreateSuccess() {
      //given
      given(jpaUserRepository.existsByUsername(userCreateRequest.username())).willReturn(false);
      given(jpaUserRepository.existsByEmail(userCreateRequest.email())).willReturn(false);
      given(passwordEncoder.encode(userCreateRequest.password())).willReturn("encodedPassword");
      given(jpaUserRepository.save(any(User.class))).willAnswer(
          invocation -> invocation.getArgument(0));
      given(userMapper.toDto(any(User.class))).willReturn(mockDto);
      //when
      UserDto result = basicUserService.create(userCreateRequest, Optional.empty());
      //then
      assertThat(result).isEqualTo(mockDto);
      then(jpaUserRepository).should().save(any(User.class));
      then(passwordEncoder).should().encode(userCreateRequest.password());
    }

    @Test
    @DisplayName("유저 생성 테스트 실패")
    void UserCreateFail() {
      //given
      given(jpaUserRepository.existsByUsername(userCreateRequest.username())).willReturn(true);
      //when,then
      assertThatThrownBy(() -> basicUserService.create(userCreateRequest, Optional.empty()))
          .isInstanceOf(UserAlreadyExistException.class);
    }

  }

  @Nested
  @DisplayName("유저 정보 수정 테스트")
  class UserUpdate {

    private UserUpdateRequest userUpdateRequest;

    @BeforeEach
    void setUp() {
      userUpdateRequest = new UserUpdateRequest("newUsername", "new@test.com", "newPassword1234");
    }

    @Test
    @DisplayName("유저 정보 수정 테스트 성공")
    void UserUpdateSuccess() {
      //given
      given(jpaUserRepository.findById(any(UUID.class))).willReturn(Optional.of(mockUser));
      given(jpaUserRepository.existsByUsername(userUpdateRequest.newUsername())).willReturn(false);
      given(jpaUserRepository.existsByEmail(userUpdateRequest.newEmail())).willReturn(false);
      given(passwordEncoder.encode(userUpdateRequest.newPassword())).willReturn(
          "newEncodedPassword");
      given(userMapper.toDto(any(User.class))).willReturn(mockDto);
      //when
      UserDto result = basicUserService.update(UUID.randomUUID(), userUpdateRequest,
          Optional.empty());
      //then
      assertThat(result).isEqualTo(mockDto);
      then(jpaUserRepository).should().findById(any(UUID.class));
      then(passwordEncoder).should().encode(userUpdateRequest.newPassword());

    }

    @Test
    @DisplayName("유저 정보 수정 테스트 실패")
    void UserUpdateFail() {
      //given
      given(jpaUserRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(
          () -> basicUserService.update(UUID.randomUUID(), userUpdateRequest, Optional.empty()))
          .isInstanceOf(UserNotFoundException.class);
    }

  }

  @Nested
  @DisplayName("유저 삭제 테스트")
  class UserDelete {

    @Test
    @DisplayName("유저 삭제 테스트 성공")
    void UserDeleteSuccess() {
      //given
      given(jpaUserRepository.findById(any(UUID.class))).willReturn(Optional.of(mockUser));
      //when
      basicUserService.delete(UUID.randomUUID());
      //then
      then(jpaUserRepository).should().findById(any(UUID.class));
      then(jpaUserRepository).should().delete(mockUser);
    }

    @Test
    @DisplayName("유저 삭제 테스트 실패")
    void UserDeleteFail() {
      //given
      given(jpaUserRepository.findById(any(UUID.class))).willReturn(Optional.empty());
      //when,then
      assertThatThrownBy(
          () -> basicUserService.delete(UUID.randomUUID())
      ).isInstanceOf(UserNotFoundException.class);
    }
  }

}