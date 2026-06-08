package com.sprint.mission.discodeit.support.fixture;

import static org.instancio.Select.field;

import com.sprint.mission.discodeit.channel.repository.qdsl.dto.ChannelDetailDto;
import com.sprint.mission.discodeit.channel.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.channel.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.constant.ChannelType;
import java.util.List;
import java.util.UUID;
import org.instancio.Instancio;

public final class ChannelFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static PublicChannelCreateRequest createPublicRequest() {
    return Instancio.create(PublicChannelCreateRequest.class);
  }

  public static PrivateChannelCreateRequest createPrivateRequest() {
    return new PrivateChannelCreateRequest(
        List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
  }

  public static Channel createPublic() {
    return baseFixture.baseUpdatableEntity(Channel.class)
        .set(field(Channel::getType), ChannelType.PUBLIC)
        .create();
  }

  public static Channel createPrivate() {
    return baseFixture.baseUpdatableEntity(Channel.class)
        .set(field(Channel::getType), ChannelType.PRIVATE)
        .ignore(field(Channel::getName))
        .ignore(field(Channel::getDescription))
        .create();
  }

  public static List<Channel> createPublicChannels() {
    return baseFixture.baseUpdatableEntities(Channel.class)
        .set(field(Channel::getType), ChannelType.PUBLIC)
        .create();
  }

  public static List<Channel> createPrivateChannels() {
    return baseFixture.baseUpdatableEntities(Channel.class)
        .set(field(Channel::getType), ChannelType.PRIVATE)
        .ignore(field(Channel::getName))
        .ignore(field(Channel::getDescription))
        .create();
  }

  public static ChannelDetailDto createChannelDetail() {
    return Instancio.of(ChannelDetailDto.class)
        .set(field(ChannelDetailDto::channel), createPublic())
        .create();
  }
}
