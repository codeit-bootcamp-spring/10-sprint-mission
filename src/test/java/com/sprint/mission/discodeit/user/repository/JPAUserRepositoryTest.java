package com.sprint.mission.discodeit.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class JPAUserRepositoryTest {

  @Autowired
  private JPAUserRepository userRepository;

  public static class UserFixture {

    public static User createUserFirst() {
      return new User("testuser", "test@email.com", "password123", null);
    }
  }

  @BeforeEach
  void setUp() {
    userRepository.save(UserFixture.createUserFirst());
  }

  @Nested
  @DisplayName("유저 이름으로 찾기 테스트")
  class ExistsByUsername {

    @Test
    @DisplayName("테스트 성공")
    void existsByUsernameSuccess() {
      assertThat(userRepository.existsByUsername("testuser")).isTrue();
    }

    @Test
    @DisplayName("테스트 실패")
    void existsByUsernameFail() {
      assertThat(userRepository.existsByUsername("noname")).isFalse();
    }
  }

  @Nested
  @DisplayName("이메일로 유저 찾기 테스트")
  class ExistsByEmail {

    @Test
    @DisplayName("테스트 성공")
    void existsByEmailSuccess() {
      assertThat(userRepository.existsByEmail("test@email.com")).isTrue();
    }

    @Test
    @DisplayName("테스트 실패")
    void existsByEmailFail() {
      assertThat(userRepository.existsByEmail("noemail@email.com")).isFalse();
    }

  }
}