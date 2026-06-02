package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.DatabaseConflictException;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.user.DuplicationUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("BasicUserService 단위 테스트")
class BasicUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private UserStatusRepository userStatusRepository;
    @Mock private ReadStatusRepository readStatusRepository;
    @Mock private ChannelRepository channelRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BasicUserService userService;

    private UUID userId;
    private UUID profileId;
    private User user;
    private UserDto.Response userResponse;
    private UserDto.CreateRequest createRequest;
    private UserDto.UpdateRequest defaultUpdateRequest;

    @BeforeEach
    void setUp() {
        // 1. 공통 데이터 초기화
        userId = UUID.randomUUID();
        profileId = UUID.randomUUID();

        user = new User("tester", "test@test.com", "hashedPassword", null);
        ReflectionTestUtils.setField(user, "id", userId);
        user.setStatus(new UserStatus(user, Instant.now()));

        userResponse = new UserDto.Response(userId, "tester", "test@test.com", null, true, Role.USER);

        // 2. 공통 요청(Request) 객체 초기화
        createRequest = new UserDto.CreateRequest("tester", "test@test.com", "password");
        defaultUpdateRequest = new UserDto.UpdateRequest("newNick", "new@test.com", "newPass");

        // 3. 공통 Mock 설정
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient().when(userRepository.saveAndFlush(any())).thenReturn(user);
        lenient().when(userMapper.toResponse(any())).thenReturn(userResponse);
    }

    @Nested
    @DisplayName("유저 생성 (create)")
    class Create {

        @Test
        @DisplayName("성공: 프로필 이미지 없이 생성")
        void create_Success_WithoutProfile() {
            // given
            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(userRepository.existsByUsername(anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn("hashedPassword");

            // when
            UserDto.Response result = userService.create(createRequest, null);

            // then
            assertThat(result).isEqualTo(userResponse);
            verify(userRepository).saveAndFlush(any());
        }

        @Test
        @DisplayName("성공: 프로필 이미지와 함께 생성")
        void create_Success_WithProfile() {
            // given
            BinaryContent profile = mock(BinaryContent.class);
            BinaryContentDto.Response profileResponse = new BinaryContentDto.Response(profileId, "test.jpg", 1L, "");
            UserDto.Response expectedUserResponse = new UserDto.Response(userId, "tester", "test@test.com", profileResponse, true, Role.USER);

            given(binaryContentRepository.findById(profileId)).willReturn(Optional.of(profile));
            given(userMapper.toResponse(any())).willReturn(expectedUserResponse);

            // when
            UserDto.Response result = userService.create(createRequest, profileId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.profile()).isNotNull();
            assertThat(result.profile().id()).isEqualTo(profileId);
        }

        @Test
        @DisplayName("실패: DB 레이스 컨디션 (Conflict)")
        void create_Fail_DatabaseConflict() {
            // given
            given(userRepository.saveAndFlush(any())).willThrow(DataIntegrityViolationException.class);

            // when & then
            assertThatThrownBy(() -> userService.create(createRequest, null))
                    .isInstanceOf(DatabaseConflictException.class);
        }

        @Test
        @DisplayName("실패: 중복된 이메일")
        void create_Fail_DuplicateEmail() {
            // given
            given(userRepository.existsByEmail(createRequest.email())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.create(createRequest, null))
                    .isInstanceOf(DuplicationUserException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 프로필 ID")
        void create_Fail_ProfileNotFound() {
            // given
            given(binaryContentRepository.findById(profileId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.create(createRequest, profileId))
                    .isInstanceOf(BinaryContentNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("유저 조회 (find/findAll)")
    class Read {
        @Test
        @DisplayName("단일 조회 성공")
        void find_Success() {
            // given & when
            UserDto.Response result = userService.find(userId);

            // then
            assertThat(result.username()).isEqualTo("tester");
        }

        @Test
        @DisplayName("단일 조회 실패: 존재하지 않는 ID")
        void find_Fail_NotFound() {
            // given
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.find(userId)).isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("전체 조회 성공 (빈 목록 포함)")
        void findAll_Success() {
            // given & when
            given(userRepository.findAll()).willReturn(List.of(user));
            List<UserDto.Response> result = userService.findAll();

            // then
            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("유저 수정 (update)")
    class Update {

        @Test
        @DisplayName("성공: 모든 정보 수정")
        void update_Success_AllFields() {
            // given
            BinaryContent newProfile = mock(BinaryContent.class);
            given(binaryContentRepository.findById(profileId)).willReturn(Optional.of(newProfile));

            // when
            userService.update(userId, defaultUpdateRequest, profileId);

            // then
            verify(userRepository).saveAndFlush(any());
        }

        @Test
        @DisplayName("성공: 본인의 기존 이메일/사용자명으로 수정 시도 시 중복 예외 미발생")
        void update_Success_OwnInfo() {
            // given
            UserDto.UpdateRequest customRequest = new UserDto.UpdateRequest("tester", "test@test.com", null);

            given(userRepository.existsByEmail("test@test.com")).willReturn(true);
            given(userRepository.existsByUsername("tester")).willReturn(true);

            // when & then
            assertThatCode(() -> userService.update(userId, customRequest, null))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("성공: 비밀번호 제외 수정 (null 체크 분기 커버)")
        void update_Success_PasswordNull() {
            // given
            UserDto.UpdateRequest customRequest = new UserDto.UpdateRequest("newNick", "new@test.com", null);

            // when
            userService.update(userId, customRequest, null);

            // then
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("실패: 수정 시 중복된 사용자명 (본인이 아닌 다른 유저가 사용 중)")
        void update_Fail_DuplicateUsername() {
            // given
            UserDto.UpdateRequest customRequest = new UserDto.UpdateRequest("otherUser", "new@test.com", null);
            given(userRepository.existsByUsername("otherUser")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.update(userId, customRequest, null))
                    .isInstanceOf(DuplicationUserException.class);
        }

        @Test
        @DisplayName("실패: DB 레이스 컨디션 (Conflict)")
        void update_Fail_DatabaseConflict() {
            // given
            given(userRepository.saveAndFlush(any())).willThrow(DataIntegrityViolationException.class);

            // when & then
            assertThatThrownBy(() -> userService.update(userId, defaultUpdateRequest, null))
                    .isInstanceOf(DatabaseConflictException.class);
        }
    }

    @Nested
    @DisplayName("유저 삭제 (delete)")
    class Delete {
        @Test
        @DisplayName("성공: 참여 중인 채널이 있는 경우")
        void delete_Success_WithChannels() {
            // given
            given(readStatusRepository.findChannelIdsByUserId(userId)).willReturn(List.of(UUID.randomUUID()));

            // when
            userService.delete(userId);

            // then
            verify(userRepository).delete(user);
            verify(channelRepository).deleteEmptyOrLonelyChannels(any());
        }

        @Test
        @DisplayName("성공: 참여 중인 채널이 없는 경우")
        void delete_Success_NoChannels() {
            // given
            given(readStatusRepository.findChannelIdsByUserId(userId)).willReturn(Collections.emptyList());

            // when
            userService.delete(userId);

            // then
            verify(channelRepository, never()).deleteEmptyOrLonelyChannels(any());
        }
    }

    @Nested
    @DisplayName("데이터 변환 (toDto)")
    class ToDto {
        @Test
        @DisplayName("성공: 정상적인 유저 엔티티")
        void toDto_Success() {
            // when
            userService.toDto(user);

            // then
            verify(userMapper).toResponse(user);
        }

        @Test
        @DisplayName("실패: 유저 상태(UserStatus) 정보가 누락된 경우")
        void toDto_Fail_StatusMissing() {
            // given
            user.setStatus(null);

            // when & then
            assertThatThrownBy(() -> userService.toDto(user))
                    .isInstanceOf(InternalServerException.class);
        }
    }
}
