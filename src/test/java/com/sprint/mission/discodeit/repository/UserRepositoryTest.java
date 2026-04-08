package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    // given
    userRepository.deleteAll();

    User user1 = new User("A", "AA", "A@gmail.com");
    user1.updateStatus(new UserStatus());
    User user2 = new User("B", "BB", "B@naver.com");
    user2.updateStatus(new UserStatus());
    User user3 = new User("C", "CC", "C@apple.com");
    user3.updateStatus(new UserStatus());
    userRepository.saveAll(List.of(user1, user2, user3));
  }

  @Nested
  class findByUsername {

    @Test
    @DisplayName("유저이름으로 User를 반환")
    void should_return_user_optional_when_username_exists() {
      // when
      Optional<User> foundUser = userRepository.findByUsername("A");

      // then
      assertThat(foundUser).hasValueSatisfying(u -> {
        assertThat(u.getUsername()).isEqualTo("A");
        assertThat(u.getEmail()).isEqualTo("A@gmail.com");
      });
    }

    @Test
    @DisplayName("일치하는 유저이름이 없으면 빈 객체를 반환")
    void should_return_empty_optional_when_username_does_not_exist() {
      // when
      Optional<User> foundUser = userRepository.findByUsername("123qwe");

      // then
      assertThat(foundUser).isEmpty();
    }
  }

  @Nested
  class findAll {

    @Test
    @DisplayName("repository에 있는 유저 전부를 반환")
    void should_return_users_from_repository() {
      // given - setUp에 유저 3명 등록됨

      // when
      List<User> users = userRepository.findAll();

      // then
      assertThat(users).hasSize(3);
    }

    @Test
    @DisplayName("userStatus가 없는 유저는 제외된채 반환")
    void should_not_return_user_when_userstatus_is_missing() {
      // given - setUp에 유저 3명 등록됨
      userRepository.save(new User("D", "DD", "D@meta.com"));

      // when
      List<User> users = userRepository.findAll();

      // then
      assertThat(users).hasSize(3);
    }

    @Test
    @DisplayName("profile 유무 상관없이 반환")
    void should_return_users_even_if_profile_is_missing() {
      // given - setUp에 유저 3명 등록됨(전부 프로필 이미지 X)
      User profileUser = new User("D", "DD", "D@meta.com");
      profileUser.updateStatus(new UserStatus());
      profileUser.updateProfile(new BinaryContent("fileName", 10, "image/png"));
      userRepository.save(profileUser);

      // when
      List<User> users = userRepository.findAll();

      // then
      assertThat(users).hasSize(4);
    }
  }

  @Nested
  class findAllById {

    @Test
    @DisplayName("repository에 있는 유저 전부 반환")
    void should_return_user_when_userIds_are_exists() {
      // given
      List<UUID> userIds = userRepository.findAll().stream()
          .map(BaseEntity::getId).toList();

      // when
      List<User> users = userRepository.findAllById(userIds);

      // then
      assertThat(users).hasSize(3);
    }

    @Test
    @DisplayName("유저ID에 있는 유저만 전부 반환")
    void should_return_only_existing_users_when_userIds_are_provided() {
      // given
      List<UUID> userIds = new java.util.ArrayList<>(userRepository.findAll().stream()
          .map(BaseEntity::getId).toList());
      userIds.add(UUID.randomUUID());

      // when
      List<User> users = userRepository.findAllById(userIds);

      // then
      assertThat(users).hasSize(3);
    }
  }
}