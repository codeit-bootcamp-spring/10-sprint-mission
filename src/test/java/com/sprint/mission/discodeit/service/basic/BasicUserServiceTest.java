package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserStatusRepository userStatusRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    BinaryContentRepository binaryContentRepository;
    @Mock
    BinaryContentStorage binaryContentStorage;
    @InjectMocks
    BasicUserService userService;

    // create
    @Test
    @DisplayName("프로필 이미지 없이 회원가입 진행시 정상적으로 성공하여 userDto 반환")
    void sign_up_without_profile_success() {
        // given
        // 기대값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        // 기대 유저 객체 생성
        User user = new User(username, email, password);
        // 기대 유저 생성 요청 dto 생성
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("fred@naver.com");
        request.setUsername("김현재");
        request.setPassword("123123");
        // 기대 유저 상태 생성
        UserStatus userStatus = new UserStatus(user);
        UserDto expectDto = new UserDto(user.getId(), username, email, null, true);

        // 동작 실행시 기대값이 나오도록 함
        // 이메일 중복 확인시 false 반환
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        // 유저 레포지토리 저장시 유저 반환
        given(userRepository.save(any(User.class))).willReturn(user);
        // 유저 상태 레포지토리 저장시 유저 상태 반환
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        // 유저 매퍼 사용시 결과 dto 반환
        given(userMapper.toDto(any(User.class),eq(true))).willReturn(expectDto);
        // when
        UserDto resultDto = userService.create(request,null);
        // then
        // dto가 널이 아닌지, 필드값이 일치한지 테스트
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getUsername()).isEqualTo(expectDto.getUsername());
        assertThat(resultDto.getEmail()).isEqualTo(expectDto.getEmail());
        assertThat(resultDto.getProfile()).isEqualTo(expectDto.getProfile());;
        assertThat(resultDto.isOnline()).isTrue();
    }

    @Test
    @DisplayName("프로필 이미지 있이 회원가입 진행시 정상적으로 성공하여 userDto 반환")
    void sign_up_with_profile_success() {
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        // 기대 바이트값
        byte[] mockByte = "바이트 데이터".getBytes();
        // 기대 유저 객체 생성
        User user = new User(username, email, password);
        // 기대 유저 생성 요청 dto 생성
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("fred@naver.com");
        request.setUsername("김현재");
        request.setPassword("123123");

        // 기대 멀티 파일 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "dumi_file.jpg",
                "image/jpg",
                mockByte);
        // 기대 바이너리 컨텐츠 생성
        UUID fakeId = UUID.randomUUID();
        BinaryContent bc = new BinaryContent(
                multipartFile.getSize(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType());
        ReflectionTestUtils.setField(bc, "id", fakeId);
        // 기대 바이너리 dto 생성
        BinaryContentDto bcDto = new BinaryContentDto(fakeId, bc.getSize(), bc.getFileName(), bc.getContentType());
        // 기대 유저 상태 생성
        UserStatus userStatus = new UserStatus(user);
        // 기대 유저 dto 생성
        UserDto expectDto = new UserDto(user.getId(), username, email, bcDto, true);

        // 동작 실행시 기대값이 나오도록 함
        // 이메일 중복 확인 시 false
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        // 바이너리 레포지토리 저장시 bc반환
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(bc);
        // 바이너리 저장소 저장시 해당 id 반환
        given(binaryContentStorage.put(eq(fakeId),eq(mockByte))).willReturn(bc.getId());
        // 유저 레포지토리 저장시 유저 반환
        given(userRepository.save(any(User.class))).willReturn(user);
        // 유저 상태 레포지토리 저장시 유저 상태 반환
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        // 유저 매퍼 사용시 결과 dto 반환
        given(userMapper.toDto(any(User.class),eq(true))).willReturn(expectDto);

        // when
        UserDto resultDto = userService.create(request, multipartFile);
        // then
        assertThat(resultDto).isNotNull();
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getUsername()).isEqualTo(expectDto.getUsername());
        assertThat(resultDto.getEmail()).isEqualTo(expectDto.getEmail());
        assertThat(resultDto.getProfile()).isEqualTo(expectDto.getProfile());
        assertThat(resultDto.isOnline()).isTrue();
        assertThat(resultDto.getProfile().getId()).isEqualTo(expectDto.getProfile().getId());
        assertThat(resultDto.getProfile().getSize()).isEqualTo(expectDto.getProfile().getSize());
        assertThat(resultDto.getProfile().getFileName()).isEqualTo(expectDto.getProfile().getFileName());
        assertThat(resultDto.getProfile().getContentType()).isEqualTo(expectDto.getProfile().getContentType());
    }

    @Test
    @DisplayName("이메일 중복으로 회원가입 진행시 실패하여 duplicateEmail 예외 발생")
    void sign_up_duplicate_email_exception_fail() {
        // given
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";

        UserCreateRequest request = new UserCreateRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword(password);

        // 동작 실행시 기대값이 나오도록 함
        // 이메일 중복 확인시 true반환
        given(userRepository.existsByEmail(anyString())).willReturn(true);
        // when
        // then
        assertThatThrownBy(() -> userService.create(request, null))
                .isInstanceOf(DuplicateEmailFoundException.class);
        // 해당 메서드들은 실행된 횟수가 0인지 확인
        then(userRepository).should(never()).save(any(User.class));
        then(userStatusRepository).should(never()).save(any(UserStatus.class));
        then(userMapper).should(never()).toDto(any(User.class),eq(true));
    }

    @Test
    @DisplayName("프로필 이미지 스토리지 저장 중 에러(IOException 등)가 발생하면, FileUploadFailException 예외가 발생한다.")
    void upload_profile_exception_fail(){
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        // 기대 바이트값
        byte[] mockByte = "바이트 데이터".getBytes();
        // 기대 유저 객체 생성
        User user = new User(username, email, password);
        // 기대 유저 생성 요청 dto 생성
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("fred@naver.com");
        request.setUsername("김현재");
        request.setPassword("123123");

        // 기대 멀티 파일 생성
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "dumi_file.jpg",
                "image/jpg",
                mockByte);
        // 기대 바이너리 컨텐츠 생성
        BinaryContent bc = new BinaryContent(
                multipartFile.getSize(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType());

        // 동작 실행시 기대값이 나오도록 함
        // 이메일 중복 확인 시 false
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        // 바이너리 레포지토리 저장시 bc반환
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(bc);
        // 바이너리 저장소에 바이트 저장시 예외 반환
        given(binaryContentStorage.put(bc.getId(),mockByte))
                .willThrow(new FileUploadFailException());
        // when
        // then
        assertThatThrownBy(() -> userService.create(request, multipartFile))
                .isInstanceOf(FileUploadFailException.class);
        // 해당 메서드들은 실행된 횟수가 0인지 확인
        then(userRepository).should(never()).save(any(User.class));
        then(userStatusRepository).should(never()).save(any(UserStatus.class));
        then(userMapper).should(never()).toDto(any(User.class),eq(true));
    }

    // update
    @Test
    @DisplayName("수정할 유저 조회 유저가 존재하지 않아서 예외를 발생해야한다.")
    void update_user_not_found_exception_fail(){
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        UUID userId = UUID.randomUUID();

        // 기대 유저 수정 요청 dto 생성
        UserUpdateRequest request = new UserUpdateRequest("현재",null,null);

        // 동작 실행시 기대값이 나오도록 함
        // 수정할 유저 조회 시 usernotfound 예외 발생
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> userService.update(userId,request,null))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("유저 프로필 없이 수정 성공해야한다")
    void update_user_without_profile_success(){
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        UUID userId = UUID.randomUUID();
        User user = new User(username, email, password);

        // 기대 유저 수정 요청 dto 생성
        UserUpdateRequest request =
                new UserUpdateRequest("현재","sonata@naver.com","1212");

        // 기대 응답 dto
        UserDto expectDto =
                new UserDto(userId, request.getNewUsername(), request.getNewEmail(), null,true);

        // 기대 유저 상태
        UserStatus userStatus = new UserStatus(user);

        // 동작 실행시 기대값이 나오도록 함
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        // 수정할 유저 조회 시 조회 성공
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(any(User.class), eq(true))).willReturn(expectDto);

        // when
        UserDto resultDto = userService.update(userId, request, null);
        // then
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getUsername()).isEqualTo(expectDto.getUsername());
        assertThat(resultDto.getEmail()).isEqualTo(expectDto.getEmail());
        assertThat(resultDto.isOnline()).isTrue();
    }

    @Test
    @DisplayName("유저 프로필 수정 성공해야한다")
    void update_user_with_profile_success(){
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        UUID userId = UUID.randomUUID();
        User user = new User(username, email, password);

        // 기대 유저 수정 요청 dto 생성
        UserUpdateRequest request = new UserUpdateRequest("현재","sonata@naver.com",null);

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "dumi_file.jpg",
                "image/jpg",
                mockByte);

        BinaryContent binaryContent = new BinaryContent(
                multipartFile.getSize(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType()
        );
        UUID bcId = UUID.randomUUID();
        // id주입
        ReflectionTestUtils.setField(binaryContent,"id", bcId);
        BinaryContentDto binaryContentDto = new BinaryContentDto(
                bcId,
                binaryContent.getSize(),
                binaryContent.getFileName(),
                binaryContent.getContentType()
        );

        // 기대 응답 dto
        UserDto expectDto =
                new UserDto(userId, request.getNewUsername(), request.getNewEmail(), binaryContentDto,true);

        // 기대 유저 상태
        UserStatus userStatus = new UserStatus(user);
        // 동작 실행시 기대값이 나오도록 함
        given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(eq(bcId), eq(mockByte))).willReturn(bcId);
        given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
        given(userMapper.toDto(any(User.class), eq(true))).willReturn(expectDto);

        // when
        UserDto resultDto = userService.update(userId,request,multipartFile);
        // then
        assertThat(resultDto.getUsername()).isEqualTo(expectDto.getUsername());
        assertThat(resultDto.getEmail()).isEqualTo(expectDto.getEmail());
        assertThat(resultDto.getId()).isEqualTo(expectDto.getId());
        assertThat(resultDto.getProfile().getId()).isEqualTo(expectDto.getProfile().getId());
        assertThat(resultDto.getProfile().getContentType()).isEqualTo(expectDto.getProfile().getContentType());
        assertThat(resultDto.getProfile().getSize()).isEqualTo(expectDto.getProfile().getSize());
        assertThat(resultDto.getProfile().getFileName()).isEqualTo(expectDto.getProfile().getFileName());
    }

    @Test
    @DisplayName("유저 수정 시 이메일 중복으로 예외 발생한다.")
    void update_user_duplicate_email_exception_fail(){
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        UUID fakeId = UUID.randomUUID();
        User user = new User(
                username,
                email,
                password
        );

        // 기대 유저 수정 요청 dto 생성
        UserUpdateRequest request = new UserUpdateRequest("현재","fred@naver.com",null);

        // 동작 실행시 기대값이 나오도록 함
        // 수정할 유저 조회 시 usernotfound 예외 발생
        given(userRepository.findById(fakeId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> userService.update(fakeId,request,null))
                .isInstanceOf(DuplicateEmailFoundException.class);
    }

    @Test
    @DisplayName("유저 수정 시 프로필 업도르중 예외가 발생한다")
    void update_user_profile_upload_exception_fail(){
        // given
        // 유저 객체 기대필드값
        String username = "김현재";
        String email = "fred@naver.com";
        String password = "123123";
        UUID userId = UUID.randomUUID();
        User user = new User(username, email, password);

        // 기대 유저 수정 요청 dto 생성
        UserUpdateRequest request = new UserUpdateRequest("현재","sonata@naver.com",null);

        byte[] mockByte = "바이트 데이터".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile(
                "profile",
                "dumi_file.jpg",
                "image/jpg",
                mockByte);

        BinaryContent binaryContent = new BinaryContent(
                multipartFile.getSize(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType()
        );
        UUID bcId = UUID.randomUUID();
        // id주입
        ReflectionTestUtils.setField(binaryContent,"id", bcId);

        // 동작 실행시 기대값이 나오도록 함
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(binaryContent);
        given(binaryContentStorage.put(eq(bcId), eq(mockByte))).willThrow(new FileUploadFailException());

        // when
        // then
        assertThatThrownBy(() -> userService.update(userId,request,multipartFile))
                .isInstanceOf(FileUploadFailException.class);
        then(userStatusRepository).should(never()).save(any(UserStatus.class));
        then(userMapper).should(never()).toDto(any(User.class),eq(true));
    }

    // 삭제
    @Test
    @DisplayName("프로필이 없는 유저의 삭제 진행시 binaryStorage 메서드 제외 한번씩 실행되어야함")
    void delete_user_without_profile_success(){
        // given
        UUID fakeId = UUID.randomUUID();
        User user = new User("김현재", "fred@naver.com", "123123");
        BinaryContent profileImg = user.getProfile();

        given(userRepository.findById(fakeId)).willReturn(Optional.of(user));
        // when
        userService.delete(fakeId);
        // then
        then(userRepository).should(times(1)).delete(any(User.class));
        then(binaryContentRepository).should(never()).delete(any(BinaryContent.class));
    }

    @Test
    @DisplayName("프로필이 있는 유저의 삭제 진행시 모든 메서드가 한번씩 실행되어야함")
    void delete_user_with_profile_success(){
        // given
        UUID fakeId = UUID.randomUUID();
        User user = new User("김현재", "fred@naver.com", "123123");
        BinaryContent profileImg = new BinaryContent(
                123123,
                "filename",
                "image/png"
        );
        ReflectionTestUtils.setField(user,"profile",profileImg);

        given(userRepository.findById(fakeId)).willReturn(Optional.of(user));
        // when
        userService.delete(fakeId);
        // then
        then(userRepository).should(times(1)).delete(any(User.class));
        then(binaryContentRepository).should(times(1)).delete(profileImg);
    }
}