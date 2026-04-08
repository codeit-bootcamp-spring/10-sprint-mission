package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.mapper.factory.MapperContainer;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;

  private final ChannelMapper channelMapper = MapperContainer.get(ChannelMapper.class);
  private BasicChannelService channelService;

  @BeforeEach
  void setUp() {
    ReadStatusMapper readStatusMapper = MapperContainer.get(ReadStatusMapper.class);
    channelService = new BasicChannelService(userRepository, channelRepository,
        readStatusRepository, channelMapper, readStatusMapper);
  }

  @Test
  @DisplayName("public channel 생성 성공")
  void success_to_create_public_channel() {
    PublicChannelCreateRequest request = ChannelFixture.createPublicRequest();
    Channel channel = channelMapper.toEntityFrom(request);
    BDDMockito.given(channelRepository.save(ArgumentMatchers.any(Channel.class)))
        .willReturn(channel);

    ChannelDto dto = channelService.createPublic(request);
    Assertions.assertThat(request)
        .extracting("name", "description", "type")
        .contains(dto.name(), dto.description(), dto.type());
  }

  @Test
  @DisplayName("private channel 생성 성공")
  void success_to_create_private_channel() {
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID()));
    Channel channel = channelMapper.toEntityFrom(request);
    BDDMockito.given(channelRepository.save(ArgumentMatchers.any(Channel.class)))
        .willReturn(channel);

    ChannelDto dto = channelService.createPrivate(request);
    Assertions.assertThat(dto)
        .extracting("name", "description", "type")
        .contains(null, null, ChannelType.PRIVATE);
  }

  @ParameterizedTest(name = "public channel 은 name, description 을 수정할 수 있고 null 이면 수정하지 않는다")
  @MethodSource("provideUpdateRequest")
  void success_to_update(PublicChannelUpdateRequest request) {
    Channel channel = Channel.builder()
        .type(request.getType())
        .name("xc,vmvb")
        .description("789456")
        .build();
    BDDMockito.given(channelRepository.findById(ArgumentMatchers.any(UUID.class)))
        .willReturn(Optional.of(channel));

    ChannelDto expected = channelService.update(UUID.randomUUID(), request);
    compareBeforeAndAfter(request.getName(), expected.name(), channel.getName());
    compareBeforeAndAfter(request.getDescription(), expected.description(),
        channel.getDescription());
  }

  private void compareBeforeAndAfter(String source, String updated, String origin) {
    if (source == null) {
      Assertions.assertThat(updated).isEqualTo(origin);
      return;
    }
    Assertions.assertThat(updated).isEqualTo(source);
  }

  private static Stream<PublicChannelUpdateRequest> provideUpdateRequest() {
    return Stream.of(
        new PublicChannelUpdateRequest(null, "ha haha"),
        new PublicChannelUpdateRequest("channel is codeit", null)
    );
  }
}
