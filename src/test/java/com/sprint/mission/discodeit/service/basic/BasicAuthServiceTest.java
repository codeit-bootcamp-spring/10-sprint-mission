package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserOnlineStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class BasicAuthServiceTest {

    @Autowired
    private BasicAuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        User user = new User("auth-user", "1234", "auth@test.com");
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user, Instant.now().minusSeconds(3600)));
        flushAndClear();

        UserDto response = authService.login(new LoginRequest("auth-user", "1234"));
        flushAndClear();

        UserStatus status = userStatusRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(response.username()).isEqualTo("auth-user");
        assertThat(status.getOnlineStatus()).isEqualTo(UserOnlineStatus.ONLINE);
    }

    @Test
    @DisplayName("존재하지 않는 username 로그인 실패")
    void login_fail_userNotFound() {
        assertThatThrownBy(() -> authService.login(new LoginRequest("not-exist", "1234")))
                .isInstanceOf(UserNotFoundException.class)
                .satisfies(ex -> {
                    DiscodeitException discodeitException = (DiscodeitException) ex;
                    assertThat(discodeitException.getErrorCode()).isEqualTo(
                            ErrorCode.USER_NOT_FOUND);
                    assertThat(discodeitException.getDetails())
                            .containsEntry("username", "not-exist");
                });
    }

    @Test
    @DisplayName("비밀번호 불일치 로그인 실패")
    void login_fail_invalidPassword() {
        User user = new User("auth-user2", "1234", "auth2@test.com");
        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user, Instant.now()));
        flushAndClear();

        assertThatThrownBy(() -> authService.login(new LoginRequest("auth-user2", "wrong")))
                .isInstanceOf(InvalidPasswordException.class)
                .satisfies(ex -> {
                    DiscodeitException discodeitException = (DiscodeitException) ex;
                    assertThat(discodeitException.getErrorCode()).isEqualTo(
                            ErrorCode.INVALID_PASSWORD);
                    assertThat(discodeitException.getDetails())
                            .containsEntry("username", "auth-user2");
                });
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
