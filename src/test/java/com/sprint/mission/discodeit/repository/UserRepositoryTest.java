package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void clearData() {
    userRepository.deleteAll();
    entityManager.flush();
  }

  @Test
  @DisplayName("이름으로 유저 존재 여부 확인 성공")
  void existsByName_Success() {
    // given
    User user = new User("ho", "ho@email.com", "password123", null);
    entityManager.persist(user);
    entityManager.flush();

    // when
    boolean result = userRepository.existsByName("ho");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("이름으로 유저 존재 여부 확인 실패 - 존재하지 않는 이름")
  void existsByName_Fail() {
    // when
    boolean result = userRepository.existsByName("nonExistentUser");

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("이메일로 유저 존재 여부 확인 성공")
  void existsByEmail_Success() {
    // given
    User user = new User("ho", "ho@email.com", "password123", null);
    entityManager.persist(user);
    entityManager.flush();

    // when
    boolean result = userRepository.existsByEmail("ho@email.com");

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("이메일로 유저 존재 여부 확인 실패 - 존재하지 않는 이메일")
  void existsByEmail_Fail() {
    // when
    boolean result = userRepository.existsByEmail("nonexistent@email.com");

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("이름으로 유저 찾기 성공")
  void findByName_Success() {
    // given
    User user = new User("ho", "ho@email.com", "password123", null);
    entityManager.persist(user);
    entityManager.flush();

    // when
    User foundUser = userRepository.findByName("ho").orElse(null);

    // then
    assertThat(foundUser).isNotNull();
    assertThat(foundUser.getName()).isEqualTo("ho");
  }

  @Test
  @DisplayName("이름으로 유저 찾기 실패 - 존재하지 않는 이름")
  void findByName_Fail() {
    // when
    User foundUser = userRepository.findByName("nonExistentUser").orElse(null);

    // then
    assertThat(foundUser).isNull();
  }

  @Test
  @DisplayName("유저 목록 페이징 및 이름 오름차순 정렬 성공")
  void findAll_PageAndSort_Success() {
    // given
    entityManager.persist(new User("ho", "ho@email.com", "password123", null));
    entityManager.persist(new User("bo", "bo@email.com", "password123", null));
    entityManager.persist(new User("hh", "hh@email.com", "password123", null));
    entityManager.flush();

    // when
    Page<User> page = userRepository.findAll(
        PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "name"))
    );

    // then
    assertThat(page.getContent()).extracting(User::getName)
        .containsExactly("bo", "hh");
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.hasNext()).isTrue();
  }

  @Test
  @DisplayName("유저 목록 페이징 실패 - 범위를 벗어난 페이지는 빈 결과")
  void findAll_PageAndSort_Fail_EmptyPage() {
    // given
    entityManager.persist(new User("ho", "ho@email.com", "password123", null));
    entityManager.flush();

    // when
    Page<User> page = userRepository.findAll(
        PageRequest.of(1, 10, Sort.by(Sort.Direction.ASC, "name"))
    );

    // then
    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalElements()).isEqualTo(1);
  }
}
