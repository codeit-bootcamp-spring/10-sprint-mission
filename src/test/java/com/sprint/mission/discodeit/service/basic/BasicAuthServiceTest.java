package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private BasicAuthService basicAuthService;

    @Test
    @DisplayName("유효한 자격증명으로 로그인 시 UserDto를 반환한다.")
    void login_success() {
        // given
        LoginRequestDto request = new LoginRequestDto("tester", "password123");
        User user = new User("tester", "test@test.com", "password123");
        UserDto expectedDto = new UserDto(UUID.randomUUID(), "tester", "test@test.com", null, true);

        given(userRepository.findByUsername("tester")).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = basicAuthService.login(request);

        // then
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("존재하지 않는 username으로 로그인 시 NoSuchElementException이 발생한다.")
    void login_fail_user_not_found() {
        // given
        LoginRequestDto request = new LoginRequestDto("ghost", "password123");
        given(userRepository.findByUsername("ghost")).willReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> basicAuthService.login(request));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 IllegalArgumentException이 발생한다.")
    void login_fail_wrong_password() {
        // given
        LoginRequestDto request = new LoginRequestDto("tester", "wrongPassword");
        User user = new User("tester", "test@test.com", "correctPassword");
        given(userRepository.findByUsername("tester")).willReturn(Optional.of(user));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> basicAuthService.login(request));
    }
}