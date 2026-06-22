package com.sprint.mission.discodeit.channel.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.channel.application.mapper.ChannelDomainMapper;
import com.sprint.mission.discodeit.channel.application.mapper.ChannelPayloadMapper;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.channel.domain.entity.constant.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ReadStatusCreatedEvent;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelErrorCode;
import com.sprint.mission.discodeit.channel.domain.exception.ChannelException;
import com.sprint.mission.discodeit.channel.domain.provider.ChannelNotifier;
import com.sprint.mission.discodeit.channel.domain.provider.ChannelUserResolver;
import com.sprint.mission.discodeit.channel.domain.repository.ChannelQueryRepository;
import com.sprint.mission.discodeit.channel.domain.repository.ChannelRepository;
import com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.presentation.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.support.mapper.DomainMapperContainer;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("ChannelService Test")
@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

  @Mock
  private ChannelRepository repository;
  @Mock
  private ChannelQueryRepository queryRepository;
  @Mock
  private ChannelUserResolver userProvider;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private ChannelNotifier notifier;

  private ChannelDomainMapper domainMapper;
  private ChannelPayloadMapper payloadMapper;

  private ChannelService channelService;

  @BeforeEach
  void setUp() {
    domainMapper = DomainMapperContainer.get(ChannelDomainMapper.class);
    payloadMapper = DomainMapperContainer.get(ChannelPayloadMapper.class);
    channelService = new ChannelService(repository, queryRepository, domainMapper, eventPublisher, userProvider, notifier, payloadMapper);
  }

  @Test
  @DisplayName("createPublic - 정상 요청 시 퍼블릭 채널이 생성되고 반환된다.")
  void createPublic_success() {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("General", "Description");

    Channel result = channelService.createPublic(request);

    verify(repository).save(any(Channel.class));
    assertThat(result.getName()).isEqualTo("General");
    assertThat(result.getType()).isEqualTo(ChannelType.PUBLIC);
    verify(notifier).notifyCreated(any());
  }

  @Test
  @DisplayName("createPrivate - 정상 요청 시 프라이빗 채널이 생성되고 참가 이벤트가 발행된다.")
  void createPrivate_success() {
    UUID participantId = UUID.randomUUID();
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(participantId));
    User user = User.builder().username("test").email("test@test.com").role(UserRole.USER).build();

    given(userProvider.getOrThrow(List.of(participantId))).willReturn(List.of(user));

    ChannelDetailDto result = channelService.createPrivate(request);

    verify(repository).save(any(Channel.class));
    verify(eventPublisher).publishEvent(any(ReadStatusCreatedEvent.class));
    verify(notifier).notifyCreated(any());
    assertThat(result.channel().getType()).isEqualTo(ChannelType.PRIVATE);
  }

  @Test
  @DisplayName("find - 존재하는 id 조회 시 정상 반환된다.")
  void find_success() {
    UUID id = UUID.randomUUID();
    Channel channel = Channel.builder().name("General").description("Desc").type(ChannelType.PUBLIC)
        .build();
    ReflectionTestUtils.setField(channel, "id", id);
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(queryRepository.findChannelDetailById(id)).willReturn(Optional.of(dto));

    ChannelDetailDto result = channelService.find(id);

    assertThat(result.channel().getId()).isEqualTo(id);
    assertThat(result.channel().getName()).isEqualTo("General");
  }

  @Test
  @DisplayName("find - 존재하지 않는 id 조회 시 예외가 발생한다.")
  void find_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(queryRepository.findChannelDetailById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.find(id))
        .isInstanceOf(ChannelException.class)
        .extracting("errorCode")
        .isEqualTo(ChannelErrorCode.CHANNELID_NOT_FOUND);
  }

  @Test
  @DisplayName("findAllByUserId - 유저가 볼 수 있는 채널 리스트를 정상 반환한다.")
  void findAllByUserId_success() {
    UUID userId = UUID.randomUUID();
    Channel channel = Channel.builder().name("General").description("Desc").type(ChannelType.PUBLIC)
        .build();
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(queryRepository.findVisibleChannelDetails(userId)).willReturn(List.of(dto));

    List<ChannelDetailDto> result = channelService.findAllByUserId(userId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).channel().getName()).isEqualTo("General");
  }

  @Test
  @DisplayName("update - 채널 정보 수정 시 정상 반영된다.")
  void update_success() {
    UUID id = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("UpdatedName",
        "UpdatedDesc");
    Channel channel = Channel.builder().name("General").description("Desc").type(ChannelType.PUBLIC)
        .build();
    ReflectionTestUtils.setField(channel, "id", id);
    ChannelDetailDto dto = new ChannelDetailDto(channel, Instant.now(), List.of());

    given(queryRepository.findChannelDetailById(id)).willReturn(Optional.of(dto));

    ChannelDetailDto result = channelService.update(id, request);

    assertThat(result.channel().getName()).isEqualTo("UpdatedName");
    assertThat(result.channel().getDescription()).isEqualTo("UpdatedDesc");
    verify(notifier).notifyUpdated(any());
  }

  @Test
  @DisplayName("delete - 유효한 id 삭제 시 정상적으로 삭제된다.")
  void delete_success() {
    UUID id = UUID.randomUUID();
    Channel channel = Channel.builder().name("General").description("Desc").type(ChannelType.PUBLIC)
        .build();
    ReflectionTestUtils.setField(channel, "id", id);

    given(repository.findById(id)).willReturn(Optional.of(channel));

    channelService.delete(id);

    verify(repository).delete(channel);
    verify(notifier).notifyDeleted(any());
  }

  @Test
  @DisplayName("delete - 존재하지 않는 id 삭제 시 예외가 발생한다.")
  void delete_fail_notFound() {
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> channelService.delete(id))
        .isInstanceOf(ChannelException.class)
        .extracting("errorCode")
        .isEqualTo(ChannelErrorCode.CHANNELID_NOT_FOUND);
  }
}
