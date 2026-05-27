package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.config.JpaAuditingConfig;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Test
  @DisplayName("성공: 이메일과 유저네임으로 유저 존재 여부를 확인한다")
  void existsBy_Success() {
    // given
    userRepository.save(new User("tester", "test@test.com", "pw", null));

    // when & then
    assertThat(userRepository.existsByEmail("test@test.com")).isTrue();
    assertThat(userRepository.existsByUsername("tester")).isTrue();
  }

  @Test
  @DisplayName("성공: findAllWithProfileAndStatus 쿼리가 연관 객체와 함께 유저 목록을 가져온다")
  void findAllWithProfileAndStatus_Success() {
    // 프로필 이미지 먼저 저장
    BinaryContent profile = new BinaryContent("profile.png", 1024L, "image/png");
    binaryContentRepository.save(profile);

    // 유저 생성
    User user1 = new User("user1", "u1@test.com", "pw", profile);

    // UserStatus 생성
    UserStatus status = new UserStatus(user1, null);
    user1.assignUserStatus(status);
    user1.assignUserStatus(status);

    // 유저 저장
    userRepository.save(user1);

    // when
    List<User> users = userRepository.findAllWithProfileAndStatus();

    // then
    assertThat(users).hasSize(1);
    assertThat(users.get(0).getUsername()).isEqualTo("user1");

    // Fetch Join으로 긁어온 프로필 이름이 맞는지 확인
    assertThat(users.get(0).getProfile().getFileName()).isEqualTo("profile.png");
  }

  @Test
  @DisplayName("성공: findByUsernameWithProfileAndStatus 쿼리가 연관 객체와 함께 단건 조회된다")
  void findByUsernameWithProfileAndStatus_Success() {
    // given
    User targetUser = new User("targetUser", "target@test.com", "pw", null); // 프로필 없음

    UserStatus status = new UserStatus(targetUser, null);
    targetUser.assignUserStatus(status);
    targetUser.assignUserStatus(status);

    userRepository.save(targetUser);

    // when
    Optional<User> result = userRepository.findByUsernameWithProfileAndStatus("targetUser");

    // then
    assertThat(result).isPresent();
    assertThat(result.get().getUsername()).isEqualTo("targetUser");
    // LEFT JOIN이라 프로필이 없어도 조회가 성공해야 함
    assertThat(result.get().getProfile()).isNull();
  }

  @Test
  @DisplayName("실패: 이미 존재하는 이메일로 유저를 저장하려 하면 예외(DataIntegrityViolationException)가 발생한다")
  void saveUser_Fail_DuplicateEmail() {
    // given
    User user1 = new User("user1", "duplicate@test.com", "pw", null);
    userRepository.save(user1);
    userRepository.flush(); // DB에 쿼리를 강제로 날려 반영함

    // when & then
    User user2 = new User("user2", "duplicate@test.com", "pw", null);

    // 동일한 이메일로 저장을 시도하면 DB Unique 제약조건 위반으로 에러가 발생해야 함
    org.junit.jupiter.api.Assertions.assertThrows(
        org.springframework.dao.DataIntegrityViolationException.class,
        () -> {
          userRepository.save(user2);
          userRepository.flush(); // 여기서 에러 발생
        }
    );
  }
}