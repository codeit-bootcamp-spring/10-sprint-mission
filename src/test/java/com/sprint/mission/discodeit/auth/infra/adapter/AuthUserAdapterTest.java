package com.sprint.mission.discodeit.auth.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthUserAdapterTest {

  @Mock
  private EntityReferenceSupplier<User> service;

  @InjectMocks
  private AuthUserAdapter adapter;

  @Test
  @DisplayName("getProxy - ID로 User 프록시 객체를 정상 반환한다.")
  void getProxy() {
    UUID id = UUID.randomUUID();
    User expected = mock(User.class);
    given(service.getProxy(id)).willReturn(expected);

    User actual = adapter.getProxy(id);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("getOrThrow - ID로 User 객체를 정상 반환한다.")
  void getOrThrow() {
    UUID id = UUID.randomUUID();
    User expected = mock(User.class);
    given(service.getOrThrow(id)).willReturn(expected);

    User actual = adapter.getOrThrow(id);

    assertThat(actual).isEqualTo(expected);
  }
}
