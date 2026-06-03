package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentFileProcessingErrorException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private JwtRegistry jwtRegistry;

    @InjectMocks
    private BasicUserService basicUserService;

    /*
        사용자 등록 테스트
     */
    // [성공]
    @Test
    @DisplayName("회원가입 성공")
    void create_user_success() throws IOException {
        // given | 테스트 준비
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 중복 검사
        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(userRepository.existsByEmail(request.email())).willReturn(false);

        // 가짜 객체 | 생성할 사용자
        UserEntity newUser = new UserEntity(
                request.username(),
                request.email(),
                request.password()
        );

        // User 생성 및 저장
        given(userMapper.toEntity(any(UserCreateRequest.class))).willReturn(newUser);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(userRepository.save(any(UserEntity.class))).willReturn(newUser);

        // 가짜 객체 | 생성할 사용자 이미지 생성 및 저장
        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "test.png",
                "image/png",
                "test".getBytes()
        );
        BinaryContentEntity profileImage = new BinaryContentEntity(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
        );
        given(binaryContentRepository.save(any(BinaryContentEntity.class))).willReturn(profileImage);

        // 가짜 응답 DTO 생성 및 저장
        BinaryContentDto expectedBinaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName(profileImage.getFileName())
                .contentType(profileImage.getContentType())
                .size(profileImage.getSize())
                .bytes(profile.getBytes())
                .build();
        UserDto expectedDto = UserDto.builder()
                .id(UUID.randomUUID())
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .profile(expectedBinaryContentDto)
                .online(true)
                .build();
        given(userMapper.toDto(any(UserEntity.class), anyBoolean())).willReturn(expectedDto);

        // when | 테스트 실행
        UserDto result = basicUserService.create(request, profile);

        // then | 테스트 검증
        assertEquals(request.username(), result.username());
        assertEquals(request.email(), result.email());
        assertEquals(profile.getOriginalFilename(), newUser.getProfile().getFileName());
    }

    // [실패] 사용자 이름 중복
    @Test
    @DisplayName("회원가입 실패 : 이미 존재하는 사용자 이름일 경우, DuplicateUsernameException 예외 발생")
    void create_user_failure_duplicate_username() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234");

        // 사용자 이름 중복
        given(userRepository.existsByUsername(request.username())).willReturn(true);

        // when
        DuplicateUsernameException exception = assertThrows(
                DuplicateUsernameException.class, () -> {
                    basicUserService.create(request, null);
                }
        );

        // then
        assertEquals(ErrorCode.DUPLICATE_USERNAME, exception.getErrorCode());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // [실패] 이메일 중복
    @Test
    @DisplayName("회원가입 실패 : 이미 존재하는 이메일일 경우, DuplicateEmailException 발생")
    void create_user_failure_duplicate_email() {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 이메일 중복
        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when
        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class, () -> {
                    basicUserService.create(request, null);
                }
        );

        // then
        assertEquals(ErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // [실패] 프로필 이미지 생성 실패
    @Test
    @DisplayName("회원가입 실패: 프로필 파일 생성에 실패할 경우, BinaryContentFileProcessingErrorException 발생")
    void create_user_failure_profile_io_exception() throws IOException {
        // given
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 가짜 객체 | 생성할 사용자
        UserEntity newUser = new UserEntity(
                request.username(),
                request.email(),
                request.password()
        );

        // 중복 검사 및 User 생성 및 저장
        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(userMapper.toEntity(request)).willReturn(newUser);
        given(userRepository.save(any(UserEntity.class))).willReturn(newUser);

        // 가짜 객체 | 생성할 프로필 이미지 정보
        MultipartFile profile = mock(MultipartFile.class);

        given(profile.isEmpty()).willReturn(false);
        given(profile.getOriginalFilename()).willReturn("test.png");
        given(profile.getSize()).willReturn(100L);
        given(profile.getContentType()).willReturn("image/png");
        given(profile.getName()).willReturn("profile");

        // 프로필 이미지 생성 오류
        given(profile.getBytes()).willThrow(new IOException("File System Error"));

        // when
        BinaryContentFileProcessingErrorException exception = assertThrows(
                BinaryContentFileProcessingErrorException.class, () -> {
                    basicUserService.create(request, profile);
                }
        );

        // then
        assertEquals(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR, exception.getErrorCode());
        verify(binaryContentStorage, never()).put(any(), any());
    }

    /*
        사용자 수정 테스트
     */
    // [성공]
    @Test
    @DisplayName("사용자 정보 수정 성공")
    void update_user_success() throws IOException {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );
        MockMultipartFile newProfileImage = new MockMultipartFile(
                "profile",
                "test.png",
                "image/png",
                "test".getBytes()
        );

        // 가짜 객체 | 기존 사용자 정보
        UserEntity targetUser = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 유효성 검증 및 중복 검사
        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
        given(userRepository.existsByEmail(request.newEmail())).willReturn(false);

        // when
        basicUserService.update(userId, request, newProfileImage);

        // then
        assertEquals(request.newUsername(), targetUser.getUsername());
        assertEquals(request.newEmail(), targetUser.getEmail());
        assertNotNull(targetUser.getProfile());                             // 미존재 -> 등록
    }

    // [실패] 다른 사용자의 이름과 중복
    @Test
    @DisplayName("사용자 정보 수정 오류: 다른 사용자 이름과 중복되면 DuplicateUsernameException 발생")
    void update_user_failure_duplicate_username() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(
                "yushi",
                null,
                null
        );

        // 가짜 객체 | 기존 사용자 정보
        UserEntity targetUser = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 유효성 검증 및 중복 검사
        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(userRepository.existsByUsername(request.newUsername())).willReturn(true);

        // when
        DuplicateUsernameException exception = assertThrows(
                DuplicateUsernameException.class, () -> {
                    basicUserService.update(userId, request, null);
                }
        );

        // then
        assertEquals(ErrorCode.DUPLICATE_USERNAME, exception.getErrorCode());
        verify(binaryContentStorage, never()).put(any(), any());
    }

    // [실패] 다른 사용자의 이메일과 중복
    @Test
    @DisplayName("사용자 정보 수정 실패: 다른 사용자 이메일과 중복될 경우, DuplicateEmailException 발생")
    void update_user_failure_duplicate_email() {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(
                "tokuno",
                "yushi@wish.com",
                null
        );

        // 가짜 객체 | 기존 사용자 정보
        UserEntity targetUser = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 유효성 검증 및 중복 검사 통과
        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
        given(userRepository.existsByEmail(request.newEmail())).willReturn(true);

        // when
        DuplicateEmailException exception = assertThrows(
                DuplicateEmailException.class, () -> {
                    basicUserService.update(userId, request, null);
                }
        );

        // then
        assertEquals(ErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
        verify(binaryContentStorage, never()).put(any(), any());
    }

    // [실패] 프로필 이미지 생성 실패
    @Test
    @DisplayName("사용자 정보 수정 실패: 새로운 이미지 생성에 실패할 경우, BinaryContentFileProcessingErrorException 발생")
    void update_user_failure_profile_io_exception() throws IOException {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );

        // 가짜 객체 | 기존 사용자 정보
        UserEntity targetUser = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // 유효성 검증 및 중복 검사
        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
        given(userRepository.existsByEmail(request.newEmail())).willReturn(false);

        // 프로필 이미지 가짜 정보
        MultipartFile profile = mock(MultipartFile.class);

        given(profile.isEmpty()).willReturn(false);
        given(profile.getOriginalFilename()).willReturn("test.png");
        given(profile.getSize()).willReturn(100L);
        given(profile.getContentType()).willReturn("image/png");
        given(profile.getName()).willReturn("profile");

        given(profile.getBytes()).willThrow(new IOException("File System Error"));

        // when
        BinaryContentFileProcessingErrorException exception = assertThrows(
                BinaryContentFileProcessingErrorException.class, () -> {
                    basicUserService.update(userId, request, profile);
                }
        );

        // then
        assertEquals(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR, exception.getErrorCode());
        verify(binaryContentStorage, never()).put(any(), any());
    }


    /*
        사용자 삭제
     */
    // [성공]
    @Test
    @DisplayName("사용자 삭제 완료")
    void user_delete_success() {
        // given
        UUID userId = UUID.randomUUID();

        // 삭제될 사용자 및 사용자의 읽음 상태 / 메시지 목록
        UserEntity targetUser = new UserEntity(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        List<ReadStatusEntity> readStatuses = List.of(new ReadStatusEntity(), new ReadStatusEntity());
        List<MessageEntity> messages = List.of(new MessageEntity(), new MessageEntity(), new MessageEntity());

        // 유효성 검증
        given(userRepository.findById(userId)).willReturn(Optional.of(targetUser));
        given(readStatusRepository.findAllByUser(targetUser)).willReturn(readStatuses);
        given(messageRepository.findByAuthor(targetUser)).willReturn(messages);

        // when
        basicUserService.delete(userId);

        // then
        verify(readStatusRepository, times(1)).deleteAll(readStatuses);
        verify(messageRepository, times(1)).deleteAll(messages);
        verify(userRepository, times(1)).delete(targetUser);
    }

    // [실패] 해당하는 사용자 미존재
    @Test
    @DisplayName("사용자 삭제 실패: 삭제하고자 하는 사용자가 존재하지 않을 경우, UserNotFoundException 발생")
    void user_delete_not_found() {
        // given
        UUID userId = UUID.randomUUID();

        // 유효성 검증 실패
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> {
                    basicUserService.delete(userId);
                }
        );

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, never()).delete(any());
    }
}
