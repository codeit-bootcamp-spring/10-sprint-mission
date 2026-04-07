package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 슬라이스 테스트")
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Nested
  @DisplayName("findByUsername 테스트")
  class FindByUsernameTest {

    @Test
    @DisplayName("존재하는 username으로 조회하면 User를 반환한다")
    void should_returnUser_when_usernameExists() {
      // given
      User user = new User("tester", "tester@test.com", "1234", null);
      UserStatus status = new UserStatus(user, Instant.now());

      userRepository.save(user);

      // when
      Optional<User> result = userRepository.findByUsername("tester");

      // then
      assertThat(result).isPresent();
      assertThat(result.get().getUsername()).isEqualTo("tester");
      assertThat(result.get().getEmail()).isEqualTo("tester@test.com");
    }

    @Test
    @DisplayName("존재하지 않는 username으로 조회하면 빈 Optional을 반환한다")
    void should_returnEmpty_when_usernameNotExists() {
      // given
      User user = new User("tester", "tester@test.com", "1234", null);
      userRepository.save(user);

      // when
      Optional<User> result = userRepository.findByUsername("jongin");

      // then
      assertThat(result).isEmpty();
    }
  }

    @Nested
    @DisplayName("existsByEmail 테스트")
    class ExistsByEmailTest {

      @Test
      @DisplayName("존재하는 이메일이면 true를 반환한다")
      void should_returnTrue_when_emailExists() {
        // given
        User user = new User("tester", "tester@test.com", "1234", null);
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByEmail("tester@test.com");

        // then
        assertThat(result).isTrue();
      }

      @Test
      @DisplayName("존재하지 않는 이메일이면 false를 반환한다")
      void should_returnFalse_when_emailNotExists() {
        // when
        boolean result = userRepository.existsByEmail("none@test.com");

        // then
        assertThat(result).isFalse();
      }
    }

    @Nested
    @DisplayName("existsByUsername 테스트")
    class ExistsByUsernameTest {

      @Test
      @DisplayName("존재하는 username이면 true를 반환한다")
      void should_return_true_when_username_exists() {
        // given
        User user = new User("tester", "tester@test.com", "1234", null);
        userRepository.save(user);

        // when
        boolean result = userRepository.existsByUsername("tester");

        // then
        assertThat(result).isTrue();
      }

      @Test
      @DisplayName("존재하지 않는 username이면 false를 반환한다")
      void should_return_false_when_username_not_exists() {
        // when
        boolean result = userRepository.existsByUsername("ghost");

        // then
        assertThat(result).isFalse();
      }
    }
}
