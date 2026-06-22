package com.sprint.mission.discodeit.user.infra.repository;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.infra.repository.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

class UserJpaRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Autowired
  private BinaryContentJpaRepository binaryContentRepository;

  private User setupUser1;
  private User setupUser2;

  @BeforeEach
  void setUp() {
    setupUser1 = createUserWithProfile("testuser1", "test1@test.com");
    setupUser2 = createUser("testuser2", "test2@test.com");
    flushAndClear();
    queryInspector.clear();
  }

  @Test
  @DisplayName("존재하는 username으로 existsByUsername 조회 시 true 반환")
  void existsByUsername_success() {
    boolean exists = userJpaRepository.existsByUsername(setupUser1.getUsername());
    Assertions.assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 username으로 existsByUsername 조회 시 false 반환")
  void existsByUsername_fail() {
    boolean exists = userJpaRepository.existsByUsername("nonexistentuser123");
    Assertions.assertThat(exists).isFalse();
  }

  @Test
  @DisplayName("존재하는 email로 existsByEmail 조회 시 true 반환")
  void existsByEmail_success() {
    boolean exists = userJpaRepository.existsByEmail(setupUser1.getEmail());
    Assertions.assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 email로 existsByEmail 조회 시 false 반환")
  void existsByEmail_fail() {
    boolean exists = userJpaRepository.existsByEmail("nonexistent@test.com");
    Assertions.assertThat(exists).isFalse();
  }


  @Test
  @DisplayName("findProfileById 조회 시 연관 엔티티가 함께 Fetch Join 되며 쿼리는 1번 발생한다 (N+1 문제 없음)")
  void findProfileById_query_count_check() {
    User fetchedUser = userJpaRepository.findProfileById(setupUser1.getId()).orElseThrow();

    // 강제로 연관관계 호출하여 추가 쿼리가 발생하는지 확인
    if (fetchedUser.getProfile() != null) {
      fetchedUser.getProfile().getFileName();
    }

    // Fetch Join을 사용했으므로 1번의 쿼리만 발생해야 한다.
    ensureQueryCount(1);
  }

  @Test
  @DisplayName("findAllUsersProfileBy 조회 시 연관 엔티티가 함께 Fetch Join 되며 쿼리는 1번 발생한다")
  void findAllUsersProfileBy_query_count_check() {
    List<User> users = userJpaRepository.findAllUsersProfileBy();
    Assertions.assertThat(users).isNotEmpty();

    for (User fetchedUser : users) {
      if (fetchedUser.getProfile() != null) {
        fetchedUser.getProfile().getFileName();
      }
    }

    ensureQueryCount(1);
  }


  @Test
  @DisplayName("유저 삭제 시 프로필 이미지, 유저 상태도 같이 삭제 된다 (Success)")
  void success_to_delete_user_with_related() {
    BinaryContent profile = setupUser1.getProfile();
    UUID userId = setupUser1.getId();

    queryInspector.clear();

    userJpaRepository.delete(setupUser1);
    flushAndClear();

    ensureQueryCount(4);

    Assertions.assertThat(userJpaRepository.existsById(userId)).isFalse();

    if (profile != null) {
      // profile should be soft-deleted by @SQLDelete
      BinaryContentStatus status =
          ((com.sprint.mission.discodeit.common.jpa.repository.DomainRepository<BinaryContent>) binaryContentRepository).findById(
                  profile.getId())
              .map(BinaryContent::getStatus)
              .orElse(null);
      Assertions.assertThat(status).isEqualTo(BinaryContentStatus.DELETED);
    }
  }

  @Test
  @DisplayName("같은 이름을 가진 유저는 저장 실패한다 (Fail)")
  void fail_to_save_user_with_same_username() {
    User userAsSameName = User.builder()
        .username(setupUser1.getUsername())
        .email("different@test.com")
        .role(UserRole.USER)
        .build();

    queryInspector.clear();

    Assertions.assertThatThrownBy(() -> userJpaRepository.saveAndFlush(userAsSameName))
        .isInstanceOf(DataIntegrityViolationException.class);

    ensureQueryCount(1);
  }

  @Test
  @DisplayName("같은 이메일을 가진 유저는 저장 실패한다 (Fail)")
  void fail_to_save_user_with_same_email() {
    User userAsSameEmail = User.builder()
        .username("differentName")
        .email(setupUser1.getEmail())
        .role(UserRole.USER)
        .build();

    queryInspector.clear();

    Assertions.assertThatThrownBy(() -> userJpaRepository.saveAndFlush(userAsSameEmail))
        .isInstanceOf(DataIntegrityViolationException.class);

    ensureQueryCount(1);
  }

  private User createUser(String username, String email) {
    User user = User.builder()
        .username(username)
        .email(email)
        .role(UserRole.USER)
        .build();
    return persistAndFlush(user);
  }

  private User createUserWithProfile(String username, String email) {
    BinaryContent profile = BinaryContent.builder()
        .fileName("profile.png")
        .size(1024L)
        .contentType("image/png")
        .build();
    User user = User.builder()
        .username(username)
        .email(email)
        .role(UserRole.USER)
        .profile(profile)
        .build();
    return persistAndFlush(user);
  }
}
