package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.common.NoChangeValueException;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BasicUserService basicUserService;

    private UUID userId;
    private String email;
    private String username;
    private String password;
    private User user;
    private UserDto expectedUserDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        email = "test@gmail.com";
        username = "test";
        password = "1234";

        user = new User(email, username, password, null);
        ReflectionTestUtils.setField(user, "id", userId);

        expectedUserDto = new UserDto(userId, username, email, null, false, Role.USER);
    }

    @Nested
    @DisplayName("사용자 등록 테스트")
    class createUser {

        @Test
        @DisplayName("프로필 없는 사용자를 등록할 수 있다.")
        void success_create_user_without_profile() {
            // given(준비)
            UserCreateRequest request = new UserCreateRequest(email, username, password);

            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
            given(userMapper.toDto(any(User.class))).willReturn(expectedUserDto);

            // when(실행)
            UserDto result = basicUserService.create(request, null);

            // then(검증)
            assertEquals(expectedUserDto, result);
            assertEquals(expectedUserDto.email(), result.email());
            assertEquals(expectedUserDto.username(), result.username());

            verify(userRepository).existsByEmail(request.email());
            verify(userRepository).existsByUsername(request.username());

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userRepository).save(argThat(user ->
                    user.getPassword().equals("encodedPassword")));

            verify(userMapper).toDto(any(User.class));
        }

        @Test
        @DisplayName("프로필 있는 사용자를 등록할 수 있다..")
        void success_create_user_with_profile() throws IOException {
            // given(준비)
            UserCreateRequest request = new UserCreateRequest(email, username, password);

            // profile Mock
            MultipartFile profileFile = mock(MultipartFile.class);
            byte[] profileBytes = "test".getBytes();

            given(profileFile.isEmpty()).willReturn(false);
            given(profileFile.getBytes()).willReturn(profileBytes);
            given(profileFile.getOriginalFilename()).willReturn("profile");
            given(profileFile.getContentType()).willReturn("image/png");

            UUID profileId = UUID.randomUUID();
            BinaryContent profile = new BinaryContent(profileFile.getOriginalFilename(), profileFile.getContentType(), profileFile.getSize());
            ReflectionTestUtils.setField(profile, "id", profileId);

            BinaryContentDto profileDto = new BinaryContentDto(profileId, profileFile.getOriginalFilename(), profileFile.getSize(), profileFile.getContentType());
            UserDto expectedUserDto = new UserDto(userId, request.username(), request.email(), profileDto, false, Role.USER);

            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(userRepository.existsByUsername(request.username())).willReturn(false);
            given(userMapper.toDto(any(User.class))).willReturn(expectedUserDto);

            // when(실행)
            UserDto result = basicUserService.create(request, profileFile);

            // then(검증)
            assertEquals(expectedUserDto, result);
            assertEquals(expectedUserDto.email(), result.email());
            assertEquals(expectedUserDto.username(), result.username());
            assertEquals(expectedUserDto.profile().size(), result.profile().size());

            verify(userRepository).existsByEmail(request.email());
            verify(userRepository).existsByUsername(request.username());

            verify(binaryContentRepository).save(any(BinaryContent.class));
            verify(binaryContentStorage).put(any(), eq(profileBytes));

            verify(userRepository).save(any(User.class));
            verify(userMapper).toDto(any(User.class));
        }

        @Test
        @DisplayName("이메일(email)이 중복되면 예외가 발생한다.")
        void fail_create_user_when_duplicated_email() {
            // given(준비)
            UserCreateRequest request = new UserCreateRequest(email, username, password);

            given(userRepository.existsByEmail(request.email())).willReturn(true);

            // when(실행), then(검증)
            assertThrows(DuplicatedEmailException.class,
                    () -> basicUserService.create(request, null));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("사용자 이름(username)이 중복되면 예외가 발생한다.")
        void fail_create_user_when_duplicated_username() {
            // given(준비)
            UserCreateRequest request = new UserCreateRequest(email, username, password);

            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(userRepository.existsByUsername(request.username())).willReturn(true);

            // when(실행), then(검증)
            assertThrows(DuplicatedUsernameException.class,
                    () -> basicUserService.create(request, null));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("프로필 업로드 실패하면 예외가 발생한다.")
        void fail_create_user_when_profile_upload() throws IOException {
            // given(준비)
            UserCreateRequest request = new UserCreateRequest(email, username, password);
            MultipartFile profile = mock(MultipartFile.class);

            given(userRepository.existsByEmail(request.email())).willReturn(false);
            given(userRepository.existsByUsername(request.username())).willReturn(false);

            given(profile.isEmpty()).willReturn(false);
            given(profile.getBytes()).willThrow(new IOException("파일 읽기 실패"));

            // when(실행), then(검증)
            assertThrows(ProfileUploadFailedException.class,
                    () -> basicUserService.create(request, profile));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userRepository, never()).save(any(User.class));
            verify(userMapper, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 단건 조회 테스트")
    class findUser {

        @Test
        @DisplayName("사용자ID로 사용자 단건 조회를 할 수 있다.")
        void success_find_user() {
            // given(준비)
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(userMapper.toDto(user)).willReturn(expectedUserDto);

            // when(실행)
            UserDto result = basicUserService.find(userId);

            // then(검증)
            assertEquals(expectedUserDto, result);
            assertEquals(expectedUserDto.email(), result.email());
            assertEquals(expectedUserDto.username(), result.username());

            verify(userRepository).findByIdWithProfile(userId);
            verify(userMapper).toDto(user);
        }

        @Test
        @DisplayName("사용자 ID가 null이면 예외가 발생한다.")
        void fail_find_user_when_userId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicUserService.find(null));

            verify(userRepository, never()).findByIdWithProfile(any());
            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("해당 ID를 가진 사용자를 찾을 수 없으면 예외가 발생한다.")
        void fail_find_user_when_user_not_found() {
            // given(준비)
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.empty());

            // when(실행),  then(검증)
            assertThrows(UserNotFoundException.class,
                    () -> basicUserService.find(userId));

            verify(userRepository).findByIdWithProfile(any());
            verify(userMapper, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 목록 조회 테스트")
    class findAllUserList {

        @Test
        @DisplayName("사용자 목록을 조회할 수 있다.")
        void success_findAll_userList() {
            // given(준비)
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();
            User user1 = new User("test1@gmail.com", "test1", "1234", null);
            User user2 = new User("test2@gmail.com", "test2", "1234", null);
            ReflectionTestUtils.setField(user1, "id", userId1);
            ReflectionTestUtils.setField(user2, "id", userId2);

            UserDto expectedUserDto1 = new UserDto(user1.getId(), user1.getUsername(), user1.getEmail(), null, true, Role.USER);
            UserDto expectedUserDto2 = new UserDto(user2.getId(), user2.getUsername(), user2.getEmail(), null, true, Role.USER);

            given(userRepository.findAllWithProfile()).willReturn(List.of(user1, user2));
            given(userMapper.toDto(eq(user1))).willReturn(expectedUserDto1);
            given(userMapper.toDto(eq(user2))).willReturn(expectedUserDto2);

            // when(실행)
            List<UserDto> result = basicUserService.findAll();

            // then(검증)
            assertEquals(2, result.size());
            assertEquals(expectedUserDto1, result.get(0));
            assertEquals(expectedUserDto2, result.get(1));

            verify(userMapper, times(2)).toDto(any(User.class));
            verify(userRepository).findAllWithProfile();
        }

        @Test
        @DisplayName("사용자가 없을 때 사용자 목록 조회 시 빈 목록을 출력할 수 있다.")
        void success_findAll_userList_when_empty_userList() {
            // give(준비)
            given(userRepository.findAllWithProfile()).willReturn(List.of());

            // when(실행)
            List<UserDto> result = basicUserService.findAll();

            // then(검증)
            assertEquals(0, result.size());

            verify(userRepository).findAllWithProfile();
            verify(userMapper, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정 테스트")
    class updateUser {
        UserUpdateRequest request;

        @BeforeEach
        void setUpUpdateUser() {
            request = new UserUpdateRequest("updateEmail@gmail.com", "12345", "updateUsername");
        }

        @Test
        @DisplayName("사용자 ID로 프로필을 제외한 사용자 정보를 수정할 수 있다.")
        void success_update_user_without_profile() {
            // given(준비)
            UserDto expectedUpdateUserDto = new UserDto(userId, "updateUsername", "updateEmail@gmail.com", null, false, Role.USER);

            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(userRepository.isEmailUsedByOther(userId, request.newEmail())).willReturn(false);
            given(userRepository.isUsernameUsedByOther(userId, request.newUsername())).willReturn(false);
            given(userMapper.toDto(user)).willReturn(expectedUpdateUserDto);

            // when(실행)
            UserDto result = basicUserService.update(userId, request,null);

            // then(검증)
            assertEquals(expectedUpdateUserDto, result);
            assertEquals(expectedUpdateUserDto.email(), result.email());
            assertEquals(expectedUpdateUserDto.username(), result.username());

            verify(userRepository).findByIdWithProfile(userId);
            verify(userRepository).isEmailUsedByOther(userId, request.newEmail());
            verify(userRepository).isUsernameUsedByOther(userId, request.newUsername());
            verify(userMapper).toDto(user);
        }

        @Test
        @DisplayName("사용자 ID로 모든 사용자 정보를 수정할 수 있다.")
        void success_update_user_with_profile() throws IOException {
            // given(준비)
            UUID oldProfileId = UUID.randomUUID();
            BinaryContent oldProfile = mock(BinaryContent.class);
            byte[] oldProfileBytes = "oldProfile".getBytes();

            when(oldProfile.getId()).thenReturn(oldProfileId);

            User user = new User(email, username, password, oldProfile);
            ReflectionTestUtils.setField(user, "id", userId);

            MultipartFile newProfileFile = mock(MultipartFile.class);
            byte[] newProfileBytes = "newProfile".getBytes();

            given(newProfileFile.isEmpty()).willReturn(false);
            given(newProfileFile.getBytes()).willReturn(newProfileBytes);
            given(newProfileFile.getOriginalFilename()).willReturn("newProfile");
            given(newProfileFile.getContentType()).willReturn("image/png");

            BinaryContentDto newProfileDto = new BinaryContentDto(null, newProfileFile.getOriginalFilename(), newProfileFile.getSize(), newProfileFile.getContentType());
            UserDto expectedUpdateUserDto = new UserDto(userId, "updateUsername", "updateEmail@gmail.com", newProfileDto, false, Role.USER);

            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(binaryContentRepository.findById(oldProfileId)).willReturn(Optional.of(oldProfile));
            given(binaryContentStorage.get(oldProfileId)).willReturn(new ByteArrayInputStream(oldProfileBytes));
            given(userRepository.isUsernameUsedByOther(userId, request.newUsername())).willReturn(false);
            given(userRepository.isEmailUsedByOther(userId, request.newEmail())).willReturn(false);
            given(userMapper.toDto(user)).willReturn(expectedUpdateUserDto);
            
            // when(실행)
            UserDto result = basicUserService.update(userId, request, newProfileFile);

            // then(검증)
            assertEquals(expectedUpdateUserDto, result);
            assertEquals(expectedUpdateUserDto.email(), result.email());
            assertEquals(expectedUpdateUserDto.username(), result.username());

            verify(userRepository).findByIdWithProfile(userId);
            verify(userRepository).isUsernameUsedByOther(userId, request.newUsername());
            verify(userRepository).isEmailUsedByOther(userId, request.newEmail());

            verify(binaryContentRepository).findById(oldProfileId);
            verify(binaryContentStorage).get(oldProfileId);

            verify(binaryContentRepository).save(any(BinaryContent.class));
            verify(binaryContentStorage).put(any(), eq(newProfileBytes));

            verify(userMapper).toDto(user);
        }

        @Test
        @DisplayName("사용자 ID가 null 이면 예외가 발생한다.")
        void fail_update_user_when_userId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicUserService.update(null, request, null));

            verify(userRepository, never()).findByIdWithProfile(any());
            verify(userRepository, never()).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).findById(any());
            verify(binaryContentStorage, never()).get(any());

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("해당 ID를 가진 사용자를 찾을 수 없으면 예외가 발생한다.")
        void fail_update_user_when_user_not_found() {
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.empty());

            // when(실행),  then(검증)
            assertThrows(UserNotFoundException.class,
                    () -> basicUserService.update(userId, request, null));

            verify(userRepository).findByIdWithProfile(any());

            verify(binaryContentRepository, never()).findById(any());
            verify(binaryContentStorage, never()).get(any());

            verify(userRepository, never()).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("기존 프로필을 찾을 수 없으면 예외가 발생한다.")
        void fail_update_user_when_profile_not_found() throws IOException {
            // given(준비)
            UUID oldProfileId = UUID.randomUUID();
            BinaryContent oldProfile = mock(BinaryContent.class);

            when(oldProfile.getId()).thenReturn(oldProfileId);

            User user = new User(email, username, password, oldProfile);
            ReflectionTestUtils.setField(user, "id", userId);

            MultipartFile newProfileFile = mock(MultipartFile.class);
            byte[] newProfileBytes = "newProfile".getBytes();

            given(newProfileFile.isEmpty()).willReturn(false);
            given(newProfileFile.getBytes()).willReturn(newProfileBytes);

            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(binaryContentRepository.findById(oldProfileId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(ProfileNotFoundException.class,
                    () -> basicUserService.update(userId, request, newProfileFile));

            verify(userRepository).findByIdWithProfile(any());

            verify(binaryContentRepository).findById(any());
            verify(binaryContentStorage, never()).get(any());

            verify(userRepository, never()).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("프로필 읽는데 실패하면 예외가 발생한다.")
        void fail_update_user_when_profile_read_fail() throws IOException {
            // given(준비)
            UUID oldProfileId = UUID.randomUUID();
            BinaryContent oldProfile = mock(BinaryContent.class);

            when(oldProfile.getId()).thenReturn(oldProfileId);

            User user = new User(email, username, password, oldProfile);
            ReflectionTestUtils.setField(user, "id", userId);

            MultipartFile newProfileFile = mock(MultipartFile.class);
            byte[] newProfileBytes = "newProfile".getBytes();

            given(newProfileFile.isEmpty()).willReturn(false);
            given(newProfileFile.getBytes()).willReturn(newProfileBytes);

            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(binaryContentRepository.findById(oldProfileId)).willReturn(Optional.of(oldProfile));

            InputStream inputStream = mock(InputStream.class);
            given(binaryContentStorage.get(oldProfileId)).willReturn(inputStream);
            given(inputStream.readAllBytes()).willThrow(new IOException("파일 읽기 실패"));

            // when(실행), then(검증)
            assertThrows(ProfileReadFailedException.class,
                    () -> basicUserService.update(userId, request, newProfileFile));

            verify(userRepository).findByIdWithProfile(any());

            verify(binaryContentRepository).findById(any());
            verify(binaryContentStorage).get(any());

            verify(userRepository, never()).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("프로필 업로드에 실패하면 예외가 발생한다.")
        void fail_update_user_when_profile_upload_fail() throws IOException {
            // given(준비)
            BinaryContent oldProfile = mock(BinaryContent.class);

            User user = new User(email, username, password, oldProfile);
            ReflectionTestUtils.setField(user, "id", userId);

            MultipartFile newProfileFile = mock(MultipartFile.class);

            given(newProfileFile.isEmpty()).willReturn(false);
            given(newProfileFile.getBytes()).willThrow(new IOException("파일 읽기 실패"));

            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));

            // when(실행), then(검증)
            assertThrows(ProfileUploadFailedException.class,
                    () -> basicUserService.update(userId, request, newProfileFile));

            verify(userRepository).findByIdWithProfile(any());

            verify(binaryContentRepository, never()).findById(any());
            verify(binaryContentStorage, never()).get(any());

            verify(userRepository, never()).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("입력값이 전부 null이면 예외가 발생한다.")
        void fail_update_user_when_request_all_field_null() {
            // given(준비)
            request = new UserUpdateRequest(null, null, null);

            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));

            // when(실행), then(검증)
            assertThrows(NoChangeValueException.class,
                    () -> basicUserService.update(userId, request, null));

            verify(userRepository).findByIdWithProfile(any());

            verify(binaryContentRepository, never()).findById(any());
            verify(binaryContentStorage, never()).get(any());

            verify(userRepository, never()).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("기존 사용자 이름이 입력된 사용자 이름과 중복되면 예외가 발생한다.")
        void fail_update_user_when_duplicated_username() {
            // given(준비)
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(userRepository.isUsernameUsedByOther(userId, request.newUsername())).willReturn(true);

            // when(실행), then(검증)
            assertThrows(DuplicatedUsernameException.class,
                    () -> basicUserService.update(userId, request, null));

            verify(userRepository).findByIdWithProfile(any());

            verify(userRepository).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository, never()).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }

        @Test
        @DisplayName("기존 이메일이 입력된 이메일과 중복되면 예외가 발생한다.")
        void fail_update_user_when_duplicated_email() {
            // given(준비)
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));
            given(userRepository.isUsernameUsedByOther(userId, request.newUsername())).willReturn(false);
            given(userRepository.isEmailUsedByOther(userId, request.newEmail())).willReturn(true);

            // when(실행), then(검증)
            assertThrows(DuplicatedEmailException.class,
                    () -> basicUserService.update(userId, request, null));

            verify(userRepository).findByIdWithProfile(any());

            verify(userRepository).isUsernameUsedByOther(any(), eq(request.newUsername()));
            verify(userRepository).isEmailUsedByOther(any(), eq(request.newEmail()));

            verify(binaryContentRepository, never()).save(any(BinaryContent.class));
            verify(binaryContentStorage, never()).put(any(), any());

            verify(userMapper, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 삭제 테스트")
    class deleteUser {

        @Test
        @DisplayName("사용자 ID로 사용자를 삭제할 수 있다")
        void success_delete_user() {
            // given(준비)
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.of(user));

            // when(실행)
            basicUserService.delete(userId);

            // then(검증)
            verify(userRepository).findByIdWithProfile(userId);
            verify(userRepository).deleteById(userId);
        }

        @Test
        @DisplayName("사용자 ID가 null이면 예외가 발생한다.")
        void delete_user_fail_when_userId_null() {
            // when(실행), then(검증)
            assertThrows(InvalidInputException.class,
                    () -> basicUserService.delete(null));

            verify(userRepository, never()).findByIdWithProfile(null);
            verify(userRepository, never()).deleteById(null);
        }

        @Test
        @DisplayName("해당 ID를 가진 사용자를 찾을 수 없으면 예외가 발생한다.")
        void delete_user_fail_when_user_not_found() {
            // given(준비)
            given(userRepository.findByIdWithProfile(userId)).willReturn(Optional.empty());

            // when(실행), then(검증)
            assertThrows(UserNotFoundException.class,
                    () -> basicUserService.delete(userId));

            verify(userRepository).findByIdWithProfile(userId);
            verify(userRepository, never()).deleteById(userId);
        }
    }
}