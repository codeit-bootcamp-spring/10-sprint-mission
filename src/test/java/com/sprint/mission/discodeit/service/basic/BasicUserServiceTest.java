package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private ReadStatusService readStatusService;

    @Mock
    private UserStatusService userStatusService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicUserService userService;

    @Test
    @DisplayName("User 생성 성공")
    void createUser_success() {
        UserCreateRequest request = new UserCreateRequest("dustle", "dustle@test.com", "1234");
        UUID userId = UUID.randomUUID();
        UserDto expected = new UserDto(userId, "dustle", "dustle@test.com", null, null);

        given(userRepository.existsByUsername(request.username())).willReturn(false);
        given(userRepository.existsByEmail(request.email())).willReturn(false);
        given(binaryContentService.createBinaryContent(null)).willReturn(null);
        given(userMapper.toDto(org.mockito.ArgumentMatchers.any(User.class))).willReturn(expected);

        UserDto result = userService.createUser(request, null);

        assertThat(result).isEqualTo(expected);
        then(userRepository).should().save(org.mockito.ArgumentMatchers.any(User.class));
        then(userStatusService).should().createUserStatus(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("username 중복 시 User 생성 실패")
    void createUser_fail_whenUsernameExists() {
        UserCreateRequest request = new UserCreateRequest("dustle", "dustle@test.com", "1234");
        given(userRepository.existsByUsername(request.username())).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(request, null))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("username");

        then(userRepository).should().existsByUsername(request.username());
        then(userRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("User 수정 성공")
    void updateUser_success() {
        UUID userId = UUID.randomUUID();
        User user = new User("before", "1234", "before@test.com");
        setId(user, userId);
        UserUpdateRequest request = new UserUpdateRequest("after", "after@test.com", "5678");
        UserDto expected = new UserDto(userId, "after", "after@test.com", null, null);

        given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));
        given(userRepository.existsByUsername("after")).willReturn(false);
        given(userRepository.existsByEmail("after@test.com")).willReturn(false);
        given(binaryContentService.createBinaryContent(null)).willReturn(null);
        given(userMapper.toDto(user)).willReturn(expected);

        UserDto result = userService.updateUser(userId, request, null);

        assertThat(result).isEqualTo(expected);
        assertThat(user.getUsername()).isEqualTo("after");
        assertThat(user.getEmail()).isEqualTo("after@test.com");
        then(userMapper).should().toDto(user);
    }

    @Test
    @DisplayName("존재하지 않는 User 수정 실패")
    void updateUser_fail_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(
                userId,
                new UserUpdateRequest("after", "after@test.com", "5678"),
                null
        )).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("User 삭제 성공")
    void deleteUser_success() {
        UUID userId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();
        UUID readStatusId = UUID.randomUUID();
        UUID userStatusId = UUID.randomUUID();

        User user = new User("dustle", "1234", "dustle@test.com");
        setId(user, userId);

        BinaryContent profile = new BinaryContent("profile.png", 10L, "image/png");
        setId(profile, profileId);
        user.updateProfile(profile);

        ChannelFixture channelFixture = new ChannelFixture();
        Message message = new Message(user, channelFixture.channel(), "hello");
        ReadStatus readStatus = new ReadStatus(user, channelFixture.channel(), Instant.now());
        setId(readStatus, readStatusId);
        UserStatus status = new UserStatus(user, Instant.now());
        setId(status, userStatusId);

        given(userRepository.findById(userId)).willReturn(java.util.Optional.of(user));

        userService.deleteUser(userId);

        then(readStatusService).should().deleteReadStatus(readStatusId);
        then(userStatusService).should().deleteUserStatus(userStatusId);
        then(binaryContentService).should().deleteBinaryContent(profileId);
        then(userRepository).should().delete(user);
        assertThat(message.getAuthor()).isNull();
        assertThat(user.getProfile()).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 User 삭제 실패")
    void deleteUser_fail_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        given(userRepository.findById(userId)).willReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    private static void setId(Object target, UUID id) {
        ReflectionTestUtils.setField(target, "id", id);
    }

    private record ChannelFixture(com.sprint.mission.discodeit.entity.Channel channel) {
        private ChannelFixture() {
            this(com.sprint.mission.discodeit.entity.Channel.buildPublic("general", "desc"));
        }
    }
}
