package com.sprint.mission.discodeit.readstatus.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("ReadStatusUserAdapter Test")
@ExtendWith(MockitoExtension.class)
class ReadStatusUserResolverAdapterTest {

  @Mock
  private EntityReferenceSupplier<User> service;

  @InjectMocks
  private ReadStatusUserAdapter adapter;

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

  @Test
  @DisplayName("getProxy - 다수의 ID로 User 프록시 리스트를 반환한다.")
  void getProxy_list_success() {
    UUID id = UUID.randomUUID();
    User expected = User.builder().username("test").email("test@test.com").role(UserRole.USER).build();
    ReflectionTestUtils.setField(expected, "id", id);
    List<UUID> ids = List.of(id);
    
    given(service.getProxy(ids)).willReturn(List.of(expected));

    List<User> actual = adapter.getProxy(ids);

    assertThat(actual).containsExactly(expected);
  }
}
