package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.TestJpaAuditConfig;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import(TestJpaAuditConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이름이 겹치지 않고 이메일이 겹치지 않는 유저는 정상 등록됩니다.")
    void save_user_success() {
        // given
        User user = new User("user", "test@test.com", "1234", null);

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(userRepository.existsByEmail("test@test.com")).isTrue();
        assertThat(userRepository.existsByUsername("user")).isTrue();
    }

    @Test
    @DisplayName("이름이 겹치는 username이면 true를 반환합니다.")
    void if_username_already_exists_return_true() {
        // given
        User user = new User("user", "test@test.com", "1234", null);
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByUsername("user");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이름이 겹치는 email이면 true를 반환합니다.")
    void if_email_already_exists_return_true() {
        // given
        User user = new User("user", "test@test.com", "1234", null);
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail("test@test.com");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 username이면 false를 반환합니다.")
    void exists_by_username_false() {
        // when
        boolean result = userRepository.existsByUsername("user");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 username이면 false를 반환합니다.")
    void exists_by_email_false() {
        // when
        boolean result = userRepository.existsByUsername("test@test");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("userId로 유저를 조회합니다.")
    void find_user_by_id() {
        // given
        User user = new User("user", "test@test.com", "1234", null);
        userRepository.save(user);

        // when
        Optional<User> savedUser = userRepository.findById(user.getId());

        // then
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getId()).isEqualTo(user.getId());
        assertThat(savedUser.get().getUsername()).isEqualTo("user");
        assertThat(savedUser.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 userId로 조회하면 empty를 반환합니다.")
    void find_user_by_id_fail() {
        // given
        UUID id = UUID.randomUUID();

        // when
        Optional<User> result = userRepository.findById(id);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("전체 유저를 조회합니다.")
    void find_all_users() {
        // given
        User user1 = new User("user1", "test1@test.com", "1234", null);
        User user2 = new User("user2", "test2@test.com", "1234", null);
        User user3 = new User("user3", "test3@test.com", "1234", null);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users).hasSize(3);

        assertThat(users)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2", "user3");
    }

    @Test
    @DisplayName("유저가 없으면 빈 리스트를 반환합니다.")
    void find_all_users_empty() {
        // when
        List<User> result = userRepository.findAll();

        // then
        assertThat(result).isEmpty();
    }
}