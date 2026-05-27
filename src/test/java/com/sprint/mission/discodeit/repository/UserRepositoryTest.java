package com.sprint.mission.discodeit.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Tag("unit")
class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    BinaryContent profile1 = BinaryContent.create("profile1", "jpg", 50);
    User user1 = User.create("test", "test@test.com", "test123", profile1);
    User user2 = User.create("test2", "test2@test.com", "test123", null);
    userRepository.saveAll(List.of(user1, user2));
  }

  @Test
  @DisplayName("성공: 사용자 이름으로 사용자 정보들의 조인 조회 성공")
  void findByUsernameSuccess() {
    //@Query("select u from User u join fetch u.userStatus left join fetch u.profile where u.username = :username and u.password =:password")
    //when
    Optional<User> result = userRepository.findByUsername("test");

    //then
    assertTrue(result.isPresent());
    User user = result.get();
    assertEquals("test", user.getUsername());
    assertEquals("profile1", user.getProfile().getFileName()); // 프로필 정보 확인
  }

  @Test
  @DisplayName("실패: 잘못된 이름으로 사용자 정보 조회 실패")
  void findByWrongUsernameAndPasswordFailure() {
    //when
    Optional<User> result = userRepository.findByUsername("wrong");

    //then
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("성공: 사용자 정보를 조인한 모든 사용자 조회 성공")
  void findAllFetchUserInfo() {
    //@Query("select u from User u join fetch u.userStatus left join fetch u.profile")
    //when
    List<User> result = userRepository.findAllFetchUserInfo();

    //then
    assertEquals(2, result.size());
    assertNotNull(result.get(0).getCreatedAt());
    assertNotNull(result.get(1).getId());
  }

  @Test
  void findByIdFetchUserInfo() {
  }
}