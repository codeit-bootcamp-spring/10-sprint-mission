package com.sprint.mission.discodeit.message.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("MessageUserResolverAdapter Test")
@ExtendWith(MockitoExtension.class)
class MessageUserResolverAdapterTest {

  @Mock
  private EntityReferenceSupplier<User> service;

  @InjectMocks
  private MessageUserResolverAdapter adapter;

  @Test
  @DisplayName("getProxyOrThrow - ID로 User 엔티티 프록시를 정상 반환한다.")
  void getProxyOrThrow_success() {
    UUID id = UUID.randomUUID();
    User expected = User.builder().username("test").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(expected, "id", id);
    given(service.getProxy(id)).willReturn(expected);

    User actual = adapter.getProxyOrThrow(id);

    assertThat(actual).isEqualTo(expected);
  }
}
