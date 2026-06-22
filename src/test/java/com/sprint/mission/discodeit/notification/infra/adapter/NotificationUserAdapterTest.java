package com.sprint.mission.discodeit.notification.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.application.supplier.UserQueryReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationUserAdapterTest {

  @Mock
  private EntityReferenceSupplier<User> service;

  @Mock
  private UserQueryReferenceSupplier userQueryReferenceSupplier;

  @InjectMocks
  private NotificationUserAdapter adapter;

  @Test
  @DisplayName("getUsername - 유저의 아이디로 유저네임을 반환한다.")
  void getUsername() {
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);
    given(user.getUsername()).willReturn("testuser");
    given(service.getOrThrow(userId)).willReturn(user);

    String result = adapter.getUsername(userId);

    assertThat(result).isEqualTo("testuser");
  }

  @Test
  @DisplayName("getProxyByUsername - 유저네임으로 유저 프록시를 반환한다.")
  void getProxyByUsername() {
    User user = mock(User.class);
    given(userQueryReferenceSupplier.getProxyByUsername("admin")).willReturn(user);

    User result = adapter.getProxyByUsername("admin");

    assertThat(result).isEqualTo(user);
  }
}
