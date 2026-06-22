package com.sprint.mission.discodeit.channel.application.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelException;
import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("ChannelReferenceSupplier Test")
@ExtendWith(MockitoExtension.class)
class ChannelReferenceSupplierTest {

  @Mock
  private EntityReferenceJpaRepository<Channel> repository;

  @InjectMocks
  private ChannelReferenceSupplier supplier;

  @Test
  @DisplayName("getProxy - 존재하는 채널 ID를 입력하면 연관된 Channel을 반환한다.")
  void getProxy_success() {
    UUID id = UUID.randomUUID();
    Channel expected = Channel.builder().name("General").description("Desc")
        .type(ChannelType.PUBLIC).build();
    ReflectionTestUtils.setField(expected, "id", id);
    given(repository.getReferenceById(id)).willReturn(expected);

    Channel actual = supplier.getProxy(id);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("existsOrThrow - 존재하지 않는 채널 ID를 입력하면 예외가 발생한다.")
  void existsOrThrow_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(repository.existsById(id)).willReturn(false);

    assertThatThrownBy(() -> supplier.existsOrThrow(id))
        .isInstanceOf(ChannelException.class)
        .extracting("errorCode")
        .isEqualTo(ChannelErrorCode.CHANNELID_NOT_FOUND);
  }

  @Test
  @DisplayName("existsOrThrow - 존재하지 않는 채널 ID 리스트를 입력하면 예외가 발생한다.")
  void existsOrThrow_collection_fail_notFound() {
    List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());
    given(repository.filterExistingIds(ids)).willReturn(List.of());

    assertThatThrownBy(() -> supplier.existsOrThrow(ids))
        .isInstanceOf(ChannelException.class)
        .extracting("errorCode")
        .isEqualTo(ChannelErrorCode.CHANNELID_NOT_FOUND);
  }
}
