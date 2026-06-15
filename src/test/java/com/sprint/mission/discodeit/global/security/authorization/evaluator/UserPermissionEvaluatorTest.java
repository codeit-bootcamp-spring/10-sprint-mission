package com.sprint.mission.discodeit.global.security.authorization.evaluator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.infra.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

class UserPermissionEvaluatorTest {

  private UserRepository repository;
  private UserPermissionEvaluator evaluator;

  @BeforeEach
  void setUp() {
    repository = mock(UserRepository.class);
    evaluator = new UserPermissionEvaluator(repository);
  }

  @Test
  @DisplayName("evaluate - 도메인 객체 이름이 일치하지 않으면 false를 반환한다.")
  void evaluate_differentDomain() {
    Authentication auth = mock(Authentication.class);
    Object targetDomainObject = new Object(); // Not User

    boolean result = evaluator.evaluate(auth, targetDomainObject, "WRITE");

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("evaluate - 도메인 객체가 User이고 WRITE 권한이며 ID가 일치하면 true를 반환한다.")
  void evaluate_writePermission_success() {
    UUID userId = UUID.randomUUID();
    Authentication auth = mock(Authentication.class);
    given(auth.getName()).willReturn(userId.toString());

    User user = mock(User.class);
    given(user.getId()).willReturn(userId);

    boolean result = evaluator.evaluate(auth, user, "WRITE");

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("evaluate - 도메인 객체가 User이고 WRITE 권한이지만 ID가 다르면 false를 반환한다.")
  void evaluate_writePermission_fail() {
    UUID userId = UUID.randomUUID();
    Authentication auth = mock(Authentication.class);
    given(auth.getName()).willReturn("other-id");

    User user = mock(User.class);
    given(user.getId()).willReturn(userId);

    boolean result = evaluator.evaluate(auth, user, "WRITE");

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("evaluate - 지원하지 않는 권한(READ 등)을 요청하면 false를 반환한다.")
  void evaluate_unsupportedPermission() {
    UUID userId = UUID.randomUUID();
    Authentication auth = mock(Authentication.class);

    User user = mock(User.class);
    given(user.getId()).willReturn(userId);

    boolean result = evaluator.evaluate(auth, user, "READ");

    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("evaluateById - ID로 엔티티를 찾아 권한을 검증한다.")
  void evaluateById_success() {
    UUID userId = UUID.randomUUID();
    Authentication auth = mock(Authentication.class);
    given(auth.getName()).willReturn(userId.toString());

    User user = mock(User.class);
    given(user.getId()).willReturn(userId);
    given(repository.findById(userId)).willReturn(Optional.of(user));

    boolean result = evaluator.evaluateById(auth, userId, "WRITE");

    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("evaluateById - 엔티티가 존재하지 않으면 false를 반환한다.")
  void evaluateById_notFound() {
    UUID userId = UUID.randomUUID();
    Authentication auth = mock(Authentication.class);

    given(repository.findById(userId)).willReturn(Optional.empty());

    boolean result = evaluator.evaluateById(auth, userId, "WRITE");

    assertThat(result).isFalse();
  }
}
