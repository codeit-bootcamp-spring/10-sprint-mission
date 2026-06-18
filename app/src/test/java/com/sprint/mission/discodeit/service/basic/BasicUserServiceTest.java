package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  AuthService authService;

  @Mock
  ApplicationEventPublisher eventPublisher;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  private BasicUserService basicUserService;

  @Test
  @DisplayName("프로필이 있는 유저 생성에 성공해야 한다.")
  void should_create_user_when_profile_is_present() {
    // given
    UserCreateRequest request = new UserCreateRequest("김코딩", "hello@hello.com", "1234");
    UUID fixedUuid = UUID.randomUUID();
    byte[] fakeImageBytes = "가짜 이미지 데이터".getBytes();
    MultipartFile multipartFile = new MockMultipartFile("file", "profile.png", "image/png",
        fakeImageBytes);
    BinaryContent expectedProfile = new BinaryContent("profile.png", fakeImageBytes.length,
        "image/png");
    User expectedUser = new User("김코딩", "hello@hello.com", "1234", expectedProfile);

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(expectedProfile);
    given(userRepository.save(any(User.class))).willReturn(expectedUser);

    // when
    basicUserService.create(request, multipartFile);

    // then
    then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
    then(userRepository).should(times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("프로필이 없는 유저 생성에 성공해야 한다.")
  void should_create_user_when_profile_is_null() {
    // given
    UserCreateRequest request = new UserCreateRequest("김코딩", "hello@hello.com", "1234");
    User expectedUser = new User("김코딩", "hello@hello.com", "1234", null);

    given(userRepository.save(any(User.class))).willReturn(expectedUser);
    // when
    basicUserService.create(request, null);

    // then
    then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
    then(userRepository).should(times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("이름이 중복되었다면 유저 생성에 실패해야 한다.")
  void should_fail_to_create_user_when_username_is_duplicated() {
    // given
    UserCreateRequest request = new UserCreateRequest("중복 이름", "hello@hello.com", "1234");
    given(userRepository.existsByUsername(request.username())).willReturn(true);
    // when, then
    assertThrows(DuplicateUsernameException.class, () -> {
      basicUserService.create(request, null);
    });
    then(userRepository).should(never()).save(any(User.class));
  }

  @Test
  @DisplayName("새로운 프로필을 포함한 수정 사항이 유저에 반영되어야 한다.")
  void should_update_user_when_new_profile_is_present() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    User existingUser = new User("구코딩", "old@hello.com", "1234", null);

    UserUpdateRequest request = new UserUpdateRequest("김코딩", "hello@hello.com", "1234");
    byte[] fakeImageBytes = "가짜 이미지 데이터".getBytes();
    MultipartFile multipartFile = new MockMultipartFile("file", "profile.png", "image/png",
        fakeImageBytes);
    BinaryContent newProfile = new BinaryContent("new-profile.png", fakeImageBytes.length,
        "image/png");

    given(userRepository.findById(fixedUuid)).willReturn(Optional.of(existingUser));

    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(newProfile);

    // when
    basicUserService.update(fixedUuid, request, multipartFile);

    // then
    assertEquals("김코딩", existingUser.getUsername());
    assertEquals("hello@hello.com", existingUser.getEmail());
    then(binaryContentRepository).should(times(1)).save(any(BinaryContent.class));
  }

  @Test
  @DisplayName("새로운 프로필이 없는 수정 사항이 유저에 반영되어야 한다.")
  void should_update_user_when_new_profile_is_null() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    User existingUser = new User("구코딩", "old@hello.com", "1234", null);

    UserUpdateRequest request = new UserUpdateRequest("김코딩", "hello@hello.com", "1234");

    given(userRepository.findById(fixedUuid)).willReturn(Optional.of(existingUser));

    // when
    basicUserService.update(fixedUuid, request, null);

    // then
    assertEquals("김코딩", existingUser.getUsername());
    assertEquals("hello@hello.com", existingUser.getEmail());
    then(binaryContentRepository).should(never()).save(any(BinaryContent.class));
  }

  @Test
  @DisplayName("수정하려는 유저가 존재하지 않으면 수정에 실패해야 한다.")
  void should_fail_update_user_when_user_not_found() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("김코딩", "hello@hello.com", "1234");

    given(userRepository.findById(any())).willReturn(Optional.empty());
    // when, then
    assertThrows(UserNotFoundException.class, () -> {
      basicUserService.update(fixedUuid, request, null);
    });
  }

  @Test
  @DisplayName("유저 삭제에 성공해야 한다.")
  void should_delete_user() {
    // given
    UUID fixedUuid = UUID.randomUUID();
    User expectedUser = new User("김코딩", "hello@hello.com", "1234", null);

    given(userRepository.findById(any())).willReturn(Optional.of(expectedUser));
    // when
    basicUserService.delete(fixedUuid);
    // then
    then(userRepository).should(times(1)).delete(expectedUser);
  }

  @Test
  @DisplayName("삭제하려는 유저가 존재하지 않으면 삭제에 실패해야 한다.")
  void should_fail_delete_user_when_user_not_found() {
    // given
    UUID fixedUuid = UUID.randomUUID();

    given(userRepository.findById(any())).willReturn(Optional.empty());
    // when, then
    assertThrows(UserNotFoundException.class, () -> {
      basicUserService.delete(fixedUuid);
    });
    then(userRepository).should(never()).delete(any(User.class));
  }
}