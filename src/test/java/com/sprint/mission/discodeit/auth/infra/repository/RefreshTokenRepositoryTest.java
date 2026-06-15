package com.sprint.mission.discodeit.auth.infra.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RefreshTokenRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("findByUser_Id - 사용자 ID로 RefreshToken을 정상 조회한다.")
  void findByUser_Id() {
    User user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .role(UserRole.USER)
        .build();
    userRepository.save(user);

    RefreshToken token = RefreshToken.builder()
        .user(user)
        .token("refresh-token-value")
        .device("mobile")
        .expiresAt(Instant.now().plusSeconds(3600))
        .build();
    refreshTokenRepository.save(token);
    flushAndClear();
    clear();

    Optional<RefreshToken> result = refreshTokenRepository.findByUser_Id(user.getId());

    assertThat(result).isPresent();
    assertThat(result.get().getToken()).isEqualTo("refresh-token-value");
    ensureQueryCount(1);
  }

  @Test
  @DisplayName("deleteAllByUser - 사용자 객체로 연관된 RefreshToken을 전부 삭제한다.")
  void deleteAllByUser() {
    User user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .role(UserRole.USER)
        .build();
    userRepository.save(user);

    RefreshToken token = RefreshToken.builder()
        .user(user)
        .token("refresh-token-value")
        .device("mobile")
        .expiresAt(Instant.now().plusSeconds(3600))
        .build();
    refreshTokenRepository.save(token);
    flushAndClear();
    clear();

    User savedUser = userRepository.findById(user.getId()).get();
    clear();
    
    refreshTokenRepository.deleteAllByUser(savedUser);
    flushAndClear();
    clear();

    Optional<RefreshToken> result = refreshTokenRepository.findByUser_Id(user.getId());
    assertThat(result).isEmpty();
  }
}
