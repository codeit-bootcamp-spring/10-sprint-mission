package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import com.sprint.mission.discodeit.support.fixture.UserFixture;
import com.sprint.mission.discodeit.support.generator.TestEntity;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.userstatus.repository.UserStatusRepository;
import com.sprint.mission.discodeit.user.entity.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@Disabled("BinaryContent schema 변화로 인해 추후 수정")
class UserRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BinaryContentRepository binaryContentRepository;

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Autowired
  private TestEntity testEntity;

  @BeforeEach
  void setUp() {
    queryInspector.clear();
  }

  @Test
  @DisplayName(
      """
          유저를 저장하고 조회한다
          profile이 존재하면 insert query가 3개, 그렇지 않으면 2개 생성된다
          조회 시 query 2개 생성된다
          """
  )
  void save_and_find_user() {
    User expected = testEntity.generatorUser();
    queryInspector.logQueries();
    ensureQueryCount(3);
    clear();

    User actual = userRepository.findById(expected.getId()).orElseThrow();
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("status", "profile")
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);

    Assertions.assertThat(actual.getStatus())
        .usingRecursiveComparison()
        .ignoringFields("user")
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected.getStatus());

    Assertions.assertThat(actual.getProfile())
        .usingRecursiveComparison()
        .comparingOnlyFields("fileName", "size", "contentType", "bytes") // 💡 핵심!
        .isEqualTo(expected.getProfile());
    queryInspector.logQueries();
    ensureQueryCount(2);
  }

  @Test
  @DisplayName("같은 이름을 가진 유저는 저장 실패한다")
  void fail_to_save_user_with_same_username() {
    User user = testEntity.generatorUser();
    clear();
    User userAsSameName = UserFixture.createEntity();
    userAsSameName.setUsername(user.getUsername());
    Assertions.assertThatThrownBy(() -> userRepository.saveAndFlush(userAsSameName))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("username");
  }

  @Test
  @DisplayName("같은 이메일을 가진 유저는 저장 실패한다")
  void fail_to_save_user_with_same_email() {
    User user = testEntity.generatorUser();
    clear();
    User userAsSameEmail = UserFixture.createEntity();
    userAsSameEmail.setEmail(user.getEmail());
    Assertions.assertThatThrownBy(() -> userRepository.saveAndFlush(userAsSameEmail))
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContaining("email");
  }

  @Test
  @DisplayName("이름, 비밀번호 같은 유저를 찾는다")
  void find_by_username_and_password() {
    User actual = testEntity.generatorUser();
    clear();
    User expected = userRepository.findByUsername(
            actual.getUsername())
        .orElse(null);
    Assertions.assertThat(actual)
        .usingRecursiveComparison()
        .withEqualsForType(this::compareInstant, Instant.class)
        .isEqualTo(expected);
  }

  @Test
  @DisplayName("이름과 비밀번호로 유저를 찾는데 이름이 잘못되면 찾을 수 없다")
  void fail_to_find_by_password_and_incorrect_username() {
    User actual = testEntity.generatorUser();
    clear();
    User expected = userRepository.findByUsername("3e4r9kx,vv")
        .orElse(null);
    Assertions.assertThat(expected).isNull();
  }

  @Test
  @DisplayName("이름과 비밀번호로 유저를 찾는데 비밀번호가 잘못되면 찾을 수 없다")
  void fail_to_find_by_username_and_incorrect_password() {
    User actual = testEntity.generatorUser();
    clear();
    User expected = userRepository.findByUsername(actual.getUsername())
        .orElse(null);
    Assertions.assertThat(expected).isNull();
  }

  @Test
  @DisplayName("주어진 id에 대해 실제 존재하는 id만 필터링 한다")
  void filterExistingIds() {
    List<User> users = List.of(
        UserFixture.createEntity(),
        UserFixture.createEntity(),
        UserFixture.createEntity()
    );
    userRepository.saveAllAndFlush(users);
    clear();
    List<UUID> fakeUUIDs = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
    List<UUID> userIds = users.stream().map(User::getId).toList();
    userIds = new ArrayList<>(userIds);
    userIds.addAll(fakeUUIDs);
    List<UUID> existingUserIds = userRepository.filterExistingIds(userIds);
    ensureQueryCount(1);
    Assertions.assertThat(userIds)
        .usingRecursiveComparison()
        .ignoringCollectionOrder()
        .isNotEqualTo(existingUserIds);
  }

  @Test
  @DisplayName(
      "유저 삭제 시 프로필 이미지, 유저 상태도 같이 삭제 된다\n"
          + "profile이 존재하면 query 3개, 그렇지 않으면 2개 생성된다"
  )
  void success_to_delete_user_with_related() {
    User user = testEntity.generatorUser();
    queryInspector.clear();
    userRepository.delete(user);
    flushAndClear();
    ensureQueryCount(3);
    UserStatus status = user.getStatus();
    BinaryContent profile = user.getProfile();
    Assertions.assertThat(userRepository.existsById(user.getId())).isFalse();
    Assertions.assertThat(binaryContentRepository.existsById(profile.getId())).isFalse();
    Assertions.assertThat(userStatusRepository.existsById(status.getId())).isFalse();
  }
}
