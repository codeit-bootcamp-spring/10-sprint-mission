package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.Role; // ⭐️ 추가됨: Role Enum
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    BinaryContentRepository binaryContentRepository;

    @Mock
    BinaryContentStorage binaryContentStorage;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthService authService;

    @InjectMocks
    BasicUserService userService;

    // ================== [CREATE 테스트] ================== //
    @Test
    @DisplayName("프로필 이미지 없이 회원가입 진행시 정상적으로 성공하여 userDto 반환")
    void sign_up_without_profile_success() {
        // given
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        // ⭐️ 변경됨: Role.USER 추가
        User user = new User(username, email, password, Role.USER);

        UserCreateRequest request = new UserCreateRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);

        UserDto expectDto = new UserDto(user.getId(), username, email, null,user.getRole(), true);

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(any(User.class), eq(true))).willReturn(expectDto);

        // when
        UserDto resultDto = userService.create(request, null);

        // then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getUsername()).isEqualTo(expectDto.getUsername());
        assertThat(resultDto.getEmail()).isEqualTo(expectDto.getEmail());
        assertThat(resultDto.getProfile()).isNull();
        assertThat(resultDto.isOnline()).isTrue();
    }

    @Test
    @DisplayName("프로필 이미지 있이 회원가입 진행시 정상적으로 성공하여 userDto 반환")
    void sign_up_with_profile_success() {
        // given
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        byte[] mockByte = "바이트 데이터".getBytes();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User(username, email, password, Role.USER);

        UserCreateRequest request = new UserCreateRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile", "dumi_file.jpg", "image/jpg", mockByte);

        UUID fakeId = UUID.randomUUID();
        BinaryContent bc = new BinaryContent(
                multipartFile.getSize(), multipartFile.getOriginalFilename(), multipartFile.getContentType());
        ReflectionTestUtils.setField(bc, "id", fakeId);

        BinaryContentDto bcDto = new BinaryContentDto(fakeId, bc.getSize(), bc.getFileName(), bc.getContentType());
        UserDto expectDto = new UserDto(user.getId(), username, email, bcDto, user.getRole(), true);

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(bc);
        given(binaryContentStorage.put(eq(fakeId), eq(mockByte))).willReturn(bc.getId());
        given(userRepository.save(any(User.class))).willReturn(user);
        given(userMapper.toDto(any(User.class), eq(true))).willReturn(expectDto);

        // when
        UserDto resultDto = userService.create(request, multipartFile);

        // then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getProfile().getId()).isEqualTo(expectDto.getProfile().getId());
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 진행시 실패하여 duplicateEmail 예외 발생")
    void sign_up_duplicate_email_exception_fail() {
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("fred@naver.com");
        request.setUsername("김현재");
        request.setPassword("123123");

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(DuplicateEmailFoundException.class);

        then(userRepository).should(never()).save(any(User.class));
        then(userMapper).should(never()).toDto(any(User.class), eq(true));
    }

    @Test
    @DisplayName("프로필 이미지 스토리지 저장 중 에러가 발생하면, FileUploadFailException 예외가 발생한다.")
    void upload_profile_exception_fail(){
        // given
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("fred@naver.com");
        request.setUsername("김현재");
        request.setPassword("123123");

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile", "dumi_file.jpg", "image/jpg", mockByte);

        BinaryContent bc = new BinaryContent(
                multipartFile.getSize(), multipartFile.getOriginalFilename(), multipartFile.getContentType());

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(bc);
        given(binaryContentStorage.put(bc.getId(), mockByte))
                .willThrow(new FileUploadFailException());

        // when & then
        assertThatThrownBy(() -> userService.create(request, multipartFile))
                .isInstanceOf(FileUploadFailException.class);

        then(userRepository).should(never()).save(any(User.class));
        then(userMapper).should(never()).toDto(any(User.class), eq(true));
    }


    // ================== [UPDATE 테스트] ================== //
    @Test
    @DisplayName("수정할 유저 조회 유저가 존재하지 않아서 예외를 발생해야한다.")
    void update_user_not_found_exception_fail(){
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("현재", null, null);

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, request, null))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 프로필 없이 수정 성공해야한다")
    void update_user_without_profile_success(){
        // given
        UUID userId = UUID.randomUUID();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User("김현재", "fred@naver.com", "123123", Role.USER);

        UserUpdateRequest request = new UserUpdateRequest("현재", "sonata@naver.com", "1212");
        UserDto expectDto = new UserDto(userId, request.getNewUsername(), request.getNewEmail(), null, user.getRole(), true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encoded1212");
        given(userMapper.toDto(any(User.class), anyBoolean())).willReturn(expectDto);
        given(authService.isOnline(any(UserDto.class))).willReturn(true);

        // when
        UserDto resultDto = userService.update(userId, request, null);

        // then
        assertThat(resultDto.getUsername()).isEqualTo(expectDto.getUsername());
        assertThat(resultDto.getEmail()).isEqualTo(expectDto.getEmail());
    }

    @Test
    @DisplayName("유저 프로필 수정 성공해야한다")
    void update_user_with_profile_success(){
        // given
        UUID userId = UUID.randomUUID();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User("김현재", "fred@naver.com", "123123", Role.USER);

        UserUpdateRequest request = new UserUpdateRequest("현재", "sonata@naver.com", null);

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile", "dumi_file.jpg", "image/jpg", mockByte);

        BinaryContent binaryContent = new BinaryContent(
                multipartFile.getSize(), multipartFile.getOriginalFilename(), multipartFile.getContentType()
        );
        UUID bcId = UUID.randomUUID();
        ReflectionTestUtils.setField(binaryContent, "id", bcId);

        BinaryContentDto binaryContentDto = new BinaryContentDto(
                bcId, binaryContent.getSize(), binaryContent.getFileName(), binaryContent.getContentType());
        UserDto expectDto = new UserDto(userId, request.getNewUsername(), request.getNewEmail(), binaryContentDto, user.getRole(),true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(eq(bcId), eq(mockByte))).willReturn(bcId);
        given(userMapper.toDto(any(User.class), anyBoolean())).willReturn(expectDto);
        given(authService.isOnline(any(UserDto.class))).willReturn(true);

        // when
        UserDto resultDto = userService.update(userId, request, multipartFile);

        // then
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getProfile().getId()).isEqualTo(expectDto.getProfile().getId());
    }

    @Test
    @DisplayName("유저 수정 시 이메일 중복으로 예외 발생한다.")
    void update_user_duplicate_email_exception_fail(){
        UUID fakeId = UUID.randomUUID();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User("김현재", "fred@naver.com", "123123", Role.USER);
        UserUpdateRequest request = new UserUpdateRequest("현재", "fred@naver.com", null);

        given(userRepository.findById(fakeId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        assertThatThrownBy(() -> userService.update(fakeId, request, null))
                .isInstanceOf(DuplicateEmailFoundException.class);
    }

    @Test
    @DisplayName("유저 수정 시 프로필 업도르중 예외가 발생한다")
    void update_user_profile_upload_exception_fail(){
        UUID userId = UUID.randomUUID();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User("김현재", "fred@naver.com", "123123", Role.USER);
        UserUpdateRequest request = new UserUpdateRequest("현재", "sonata@naver.com", null);

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile", "dumi_file.jpg", "image/jpg", mockByte);

        BinaryContent binaryContent = new BinaryContent(
                multipartFile.getSize(), multipartFile.getOriginalFilename(), multipartFile.getContentType()
        );
        UUID bcId = UUID.randomUUID();
        ReflectionTestUtils.setField(binaryContent, "id", bcId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(eq(bcId), eq(mockByte))).willThrow(new FileUploadFailException());

        assertThatThrownBy(() -> userService.update(userId, request, multipartFile))
                .isInstanceOf(FileUploadFailException.class);

        then(userMapper).should(never()).toDto(any(User.class), anyBoolean());
    }


    // ================== [DELETE 테스트] ================== //
    @Test
    @DisplayName("프로필이 없는 유저의 삭제 진행시 binaryStorage 메서드 제외 한번씩 실행되어야함")
    void delete_user_without_profile_success(){
        UUID fakeId = UUID.randomUUID();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User("김현재", "fred@naver.com", "123123", Role.USER);

        given(userRepository.findById(fakeId)).willReturn(Optional.of(user));

        userService.delete(fakeId);

        then(userRepository).should(times(1)).delete(any(User.class));
        then(binaryContentRepository).should(never()).delete(any(BinaryContent.class));
    }

    @Test
    @DisplayName("프로필이 있는 유저의 삭제 진행시 모든 메서드가 한번씩 실행되어야함")
    void delete_user_with_profile_success(){
        UUID fakeId = UUID.randomUUID();

        // ⭐️ 변경됨: Role.USER 추가
        User user = new User("김현재", "fred@naver.com", "123123", Role.USER);
        BinaryContent profileImg = new BinaryContent(123123, "filename", "image/png");
        ReflectionTestUtils.setField(user, "profile", profileImg);

        given(userRepository.findById(fakeId)).willReturn(Optional.of(user));

        userService.delete(fakeId);

        then(userRepository).should(times(1)).delete(any(User.class));
        then(binaryContentRepository).should(times(1)).delete(profileImg);
    }
}