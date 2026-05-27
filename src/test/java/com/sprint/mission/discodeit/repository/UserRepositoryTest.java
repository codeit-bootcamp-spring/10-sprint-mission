package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(AppConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("이메일 조회 성공: 존재하는 이메일로 사용자를 조회할 수 있어야 합니다.")
  void findByEmail_success() {
    // given
    User user = new User("test", "test@email.com", "test1234");
    userRepository.save(user);

    // when
    Optional<User> found = userRepository.findByEmail("test@email.com");

    // then
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo("test@email.com");
  }

  @Test
  @DisplayName("상세 조회 실패: 존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다.")
  void findWithDetailById_fail() {
    // given
    UUID id = UUID.randomUUID();

    // when
    Optional<User> found = userRepository.findWithDetailsById(id);

    // then
    assertThat(found).isEmpty();
  }

}
