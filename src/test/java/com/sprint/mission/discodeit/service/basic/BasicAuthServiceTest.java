package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.UserSessionManager;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class BasicAuthServiceTest {

  @Mock
  private UserSessionManager userSessionManager;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Spy
  PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @InjectMocks
  private BasicAuthService authService;

  @Test
  @DisplayName("사용자 권한을 변경한다.")
  void updateRoleSuccess() {

    UUID userId = UUID.randomUUID();
    User user = User.create("oldName", "old@test.com", passwordEncoder.encode("oldPass"), null);
    UserDto userDto = new UserDto(userId, null, null, Role.CHANNEL_MANAGER, null, true,
        Instant.now(), Instant.now());
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, Role.CHANNEL_MANAGER);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(eq(user), any(boolean.class))).willReturn(userDto);

    assertEquals(Role.USER, user.getRole());//기본값

    //when
    UserDto result = authService.updateRole(request);
    //then
    assertNotNull(result);
    assertAll(
        () -> assertEquals(Role.CHANNEL_MANAGER, user.getRole()),
        () -> assertEquals(Role.CHANNEL_MANAGER, result.role()),
        () -> then(userSessionManager).should(times(1)).setOffline(eq(user))
    );


  }
}