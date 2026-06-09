package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.cache.UserCacheService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.util.UserSessionManager;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentMapper binaryContentMapper;
  @Mock
  private BinaryContentStorage binaryContentStorage;
  @Spy
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  @Mock
  private UserSessionManager userSessionManager;
  @Mock
  private ApplicationEventPublisher applicationEventPublisher;
  @Mock
  private UserCacheService userCacheService;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("실패: 중복된 사용자 이메일에 대해 사용자 생성 실패")
  void CreateUserFailInDuplicatedUserEmail() {
    //given
    UserCreateRequest dto = new UserCreateRequest("user", "email@test.com", "password");
    given(userRepository.existsByEmail(dto.email())).willReturn(true);

    //when & then
    assertThrows(EmailAlreadyExistException.class,
        () -> userService.create(dto, Optional.empty()));
  }

  @Test
  @DisplayName("성공: 프로필 이미지를 가진 사용자 생성 성공")
  void CreateUserSuccessWithProfileImage() {

    //given
    UserCreateRequest dto = new UserCreateRequest("user", "email@test.com", "password");
    BinaryContentCreateDto profileDto = new BinaryContentCreateDto("fileName", "jpg",
        new byte[]{1, 3}, 50);
    BinaryContent profile = BinaryContent.create(profileDto.fileName(),
        profileDto.contentType(), profileDto.size());
    User user = User.create(dto.username(), dto.email(), passwordEncoder.encode(dto.password()),
        profile);
    BinaryContentDto binaryContentDto = new BinaryContentDto(UUID.randomUUID(),
        profile.getFileName(), profile.getSize(), profile.getContentType(),
        BinaryContentStatus.PROCESSING);
    UserDto userDto = new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(), Role.USER,
        binaryContentDto, true, Instant.now(), Instant.now());
    List<Channel> channels = List.of(new Channel[]{mock(Channel.class), mock(Channel.class)});

    given(userRepository.existsByEmail(dto.email())).willReturn(false);
    given(userRepository.existsByUsername(dto.username())).willReturn(false);
    given(binaryContentMapper.toEntity(profileDto)).willReturn(profile);
    given(userMapper.toEntity(eq(dto), any(), eq(profile))).willReturn(
        user);
    given(channelRepository.findAllPublic()).willReturn(channels);
    given(userSessionManager.isOnline(user)).willReturn(true);
    given(userMapper.toDto(any(User.class), any(boolean.class))).willReturn(userDto);

    //when
    UserDto result = userService.create(dto, Optional.of(profileDto));

    //then
    assertNotNull(result);
    assertEquals(result.username(), dto.username());

    // 유저 저장 검증
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    then(userRepository).should().save(userCaptor.capture());
    User capturedUser = userCaptor.getValue();
    assertEquals(capturedUser.getUsername(), dto.username());
    assertEquals(capturedUser.getEmail(), dto.email());
    assertTrue(passwordEncoder.matches(dto.password(), capturedUser.getPassword()));

    // 읽기 상태 저장 검증
    ArgumentCaptor<List<ReadStatus>> listCaptor = ArgumentCaptor.forClass(List.class);
    then(readStatusRepository).should().saveAll(listCaptor.capture());
    assertEquals(channels.size(), listCaptor.getValue().size());
    then(applicationEventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
  }

  @Test
  @DisplayName("성공: 프로필 이미지가 없는 사용자 생성 성공")
  void CreateUserSuccessWithoutProfileImage() {

    //given
    UserCreateRequest dto = new UserCreateRequest("user", "email@test.com", "password");
    User user = User.create(dto.username(), dto.email(), passwordEncoder.encode(dto.password()),
        null);
    UserDto userDto = new UserDto(UUID.randomUUID(), user.getUsername(), user.getEmail(), Role.USER,
        null, true, Instant.now(), Instant.now());
    List<Channel> channels = List.of(new Channel[]{mock(Channel.class), mock(Channel.class)});

    given(userRepository.existsByEmail(dto.email())).willReturn(false);
    given(userRepository.existsByUsername(dto.username())).willReturn(false);
    given(userMapper.toEntity(eq(dto), any(), isNull())).willReturn(user);
    given(channelRepository.findAllPublic()).willReturn(channels);
    given(userSessionManager.isOnline(user)).willReturn(true);
    given(userMapper.toDto(any(User.class), any(boolean.class))).willReturn(userDto);

    //when
    UserDto result = userService.create(dto, Optional.empty());

    //then
    assertNotNull(result);
    assertEquals(result.username(), dto.username());

    // 유저 저장 검증
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    then(userRepository).should().save(userCaptor.capture());
    User capturedUser = userCaptor.getValue();
    assertEquals(capturedUser.getUsername(), dto.username());
    assertEquals(capturedUser.getEmail(), dto.email());
    assertTrue(passwordEncoder.matches(dto.password(), capturedUser.getPassword()));

    // 읽기 상태 저장 검증
    ArgumentCaptor<List<ReadStatus>> listCaptor = ArgumentCaptor.forClass(List.class);
    then(readStatusRepository).should().saveAll(listCaptor.capture());
    assertEquals(channels.size(), listCaptor.getValue().size());
  }

  @Test
  @DisplayName("성공: 사용자 정보(이름, 이메일, 비밀번호, 프로필) 수정 성공")
  void updateUserSuccess() {
    //given
    UUID userId = UUID.randomUUID();
    User existingUser = User.create("oldName", "old@test.com", passwordEncoder.encode("oldPass"),
        null);
    UserUpdateRequest updateDto = new UserUpdateRequest("newName", "new@test.com", "newPass");
    BinaryContentCreateDto binaryContentCreateDto = new BinaryContentCreateDto("fileName", "jpg",
        new byte[]{1, 2}, 50);
    UserDto userDto = new UserDto(userId, "newName", "old@test.com", Role.USER, null, true,
        Instant.now(),
        Instant.now());
    BinaryContent profile = BinaryContent.create(binaryContentCreateDto.fileName(),
        binaryContentCreateDto.contentType(), binaryContentCreateDto.size());

    given(userRepository.findByIdFetchUserInfo(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByUsername(updateDto.username())).willReturn(false);
    given(userRepository.existsByEmail(updateDto.email())).willReturn(false);
    given(userSessionManager.isOnline(existingUser)).willReturn(true);
    given(userMapper.toDto(eq(existingUser), any(boolean.class))).willReturn(userDto);
    given(binaryContentMapper.toEntity(binaryContentCreateDto)).willReturn(profile);

    //when
    UserDto result = userService.update(userId, updateDto, Optional.of(binaryContentCreateDto));

    //then
    assertNotNull(result);
    assertEquals(updateDto.username(), existingUser.getUsername());
    assertTrue(passwordEncoder.matches(updateDto.password(), existingUser.getPassword()));

    then(binaryContentRepository).should().save(profile);
    then(applicationEventPublisher).should().publishEvent(any(BinaryContentCreatedEvent.class));
  }

  @Test
  @DisplayName("성공: 사용자 정보 비밀번호 수정 성공")
  void updateUserPasswordSuccess() {
    //given
    UUID userId = UUID.randomUUID();
    User existingUser = User.create("oldName", "old@test.com", passwordEncoder.encode("oldPass"),
        null);
    UserUpdateRequest updateDto = new UserUpdateRequest(null, null, "newPass");
    UserDto userDto = new UserDto(userId, null, null, Role.USER, null, true, Instant.now(),
        Instant.now());

    given(userRepository.findByIdFetchUserInfo(userId)).willReturn(Optional.of(existingUser));
    given(userSessionManager.isOnline(existingUser)).willReturn(true);
    given(userMapper.toDto(eq(existingUser), any(boolean.class))).willReturn(userDto);

    //when
    UserDto result = userService.update(userId, updateDto, Optional.empty());

    //then
    assertNotNull(result);
    assertEquals("oldName", existingUser.getUsername());
    assertEquals("old@test.com", existingUser.getEmail());
    assertTrue(passwordEncoder.matches(updateDto.password(), existingUser.getPassword()));

  }

  @Test
  @DisplayName("성공: 사용자 이름만 수정 성공")
  void updateUserNameOnlySuccess() {
    //given
    UUID userId = UUID.randomUUID();
    User existingUser = User.create("oldName", "old@test.com", passwordEncoder.encode("oldPass"),
        null);
    UserUpdateRequest updateDto = new UserUpdateRequest("newName", null, null);
    UserDto userDto = new UserDto(userId, updateDto.username(), existingUser.getEmail(), Role.USER,
        null, true,
        Instant.now(),
        Instant.now());

    given(userRepository.findByIdFetchUserInfo(userId)).willReturn(Optional.of(existingUser));
    given(userSessionManager.isOnline(existingUser)).willReturn(true);
    given(userMapper.toDto(eq(existingUser), any(boolean.class))).willReturn(userDto);

    //when
    UserDto result = userService.update(userId, updateDto, Optional.empty());

    //then
    assertNotNull(result);
    assertEquals("newName", existingUser.getUsername());
    assertEquals("old@test.com", existingUser.getEmail());
    assertTrue(passwordEncoder.matches("oldPass", existingUser.getPassword()));

  }

  @Test
  @DisplayName("실패: 이미 존재하는 이메일로 사용자 정보 수정 실패")
  void updateUserFailure() {
    //given
    UUID userId = UUID.randomUUID();
    User existingUser = User.create("oldName", "old@test.com", passwordEncoder.encode("oldPass"),
        null);
    UserUpdateRequest updateDto = new UserUpdateRequest("newName", "exist@test.com", "newPass");
    Optional<BinaryContentCreateDto> binaryContentCreateDto = Optional.empty();

    given(userRepository.findByIdFetchUserInfo(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(updateDto.email())).willReturn(true);

    //when & then
    assertThrows(EmailAlreadyExistException.class,
        () -> userService.update(userId, updateDto, binaryContentCreateDto));
  }

  @Test
  @DisplayName("성공: 사용자 삭제 성공")
  void deleteUserSuccess() {
    //given
    UUID userId = UUID.randomUUID();
    User user = User.create("oldName", "old@test.com", passwordEncoder.encode("oldPass"), null);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    //when
    userService.delete(userId);

    //then
    InOrder inOrder = inOrder(readStatusRepository, userRepository);

    inOrder.verify(readStatusRepository).deleteByUserId(userId); // 1번: 읽기 상태 삭제
    inOrder.verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("실패: 유효하지 않은 사용자 삭제 실패")
  void deleteUserFailure() {
    //given
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    //when & then
    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
  }

}