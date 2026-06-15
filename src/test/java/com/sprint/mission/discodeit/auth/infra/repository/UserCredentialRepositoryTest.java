package com.sprint.mission.discodeit.auth.infra.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserCredentialRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private UserCredentialRepository userCredentialRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("findByUser_Id - 사용자 ID로 UserCredential을 정상 조회한다.")
  void findByUser_Id() {
    User user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .role(com.sprint.mission.discodeit.user.domain.entity.constant.UserRole.USER)
        .build();
    userRepository.save(user);

    UserCredential credential = UserCredential.builder()
        .user(user)
        .password("encoded-password")
        .build();
    userCredentialRepository.save(credential);
    flushAndClear();
    clear();

    Optional<UserCredential> result = userCredentialRepository.findByUser_Id(user.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getPassword()).isEqualTo("encoded-password");
    assertThat(result.get().getUser().getUsername()).isEqualTo("testuser");
    ensureQueryCount(1);
  }

  @Test
  @DisplayName("findByUser_Email - 사용자 이메일로 UserCredential을 정상 조회한다.")
  void findByUser_Email() {
    User user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .role(com.sprint.mission.discodeit.user.domain.entity.constant.UserRole.USER)
        .build();
    userRepository.save(user);

    UserCredential credential = UserCredential.builder()
        .user(user)
        .password("encoded-password")
        .build();
    userCredentialRepository.save(credential);
    flushAndClear();
    clear();

    Optional<UserCredential> result = userCredentialRepository.findByUser_Email("test@example.com");

    assertThat(result).isPresent();
    assertThat(result.get().getPassword()).isEqualTo("encoded-password");
    assertThat(result.get().getUser().getUsername()).isEqualTo("testuser");
    ensureQueryCount(1);
  }
}
