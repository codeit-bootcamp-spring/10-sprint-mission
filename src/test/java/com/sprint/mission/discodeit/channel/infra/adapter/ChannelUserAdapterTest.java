package com.sprint.mission.discodeit.channel.infra.adapter;

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

@DisplayName("ChannelUserAdapter Test")
@ExtendWith(MockitoExtension.class)
class ChannelUserAdapterTest {

  @Mock
  private EntityReferenceSupplier<User> service;

  @InjectMocks
  private ChannelUserAdapter adapter;

  @Test
  @DisplayName("getOrThrow - ID 리스트로 User 리스트를 정상적으로 반환한다.")
  void getOrThrow_success() {
    UUID id = UUID.randomUUID();
    User expected = User.builder().username("test").email("test@test.com").role(UserRole.USER)
        .build();
    ReflectionTestUtils.setField(expected, "id", id);
    List<UUID> ids = List.of(id);

    given(service.getOrThrow(ids)).willReturn(List.of(expected));

    List<User> actual = adapter.getOrThrow(ids);

    assertThat(actual).containsExactly(expected);
  }
}
