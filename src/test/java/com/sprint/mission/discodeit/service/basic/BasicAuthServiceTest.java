package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusRepository userStatusRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BasicAuthService basicAuthService;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        User user = new User("testuser", "test@example.com", "password", null);
        UUID userId = user.getId();
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        UserDto expectedDto = UserDto.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .online(true)
                .build();

        given(userRepository.findByName("testuser")).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(new UserStatus(user, Instant.now())));
        given(userMapper.toDto(user, true)).willReturn(expectedDto);

        // when
        UserDto result = basicAuthService.login(loginRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(expectedDto.id());
        assertThat(result.username()).isEqualTo(expectedDto.username());
        assertThat(result.online()).isEqualTo(expectedDto.online());
        verify(userRepository).findByName("testuser");
        verify(userStatusRepository).findByUser_Id(userId);
        verify(userMapper).toDto(user, true);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_userNotFound() {
        // given
        LoginRequest wrongUserRequest = new LoginRequest("unknownuser", "password");
        given(userRepository.findByName("unknownuser")).willReturn(Optional.empty());


        // when & then
        assertThatThrownBy(() -> basicAuthService.login(wrongUserRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_passwordMismatch() {
        // given
        User user = new User("testuser", "test@example.com", "password", null);
        LoginRequest wrongPasswordRequest = new LoginRequest("testuser", "wrongpassword");
        given(userRepository.findByName("testuser")).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> basicAuthService.login(wrongPasswordRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }
}
