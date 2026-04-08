package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.stream.IntStream;
import net.datafaker.Faker;

public final class ChannelFixture {

  private static final Faker faker = new Faker();

  private ChannelFixture() {
  }

  public static PublicChannelCreateRequest createPublicRequest() {
    return new PublicChannelCreateRequest(getName(), getDescription());
  }

  private static String getDescription() {
    return faker.text().text();
  }

  private static String getName() {
    return faker.funnyName().name();
  }

  public static Channel createPublic() {
    return new Channel(ChannelType.PUBLIC, getName(), getDescription());
  }

  public static Channel createPrivate() {
    return new Channel(ChannelType.PRIVATE, null, null);
  }

  public static List<Channel> createPublicChannels() {
    return IntStream.range(0, 3)
        .mapToObj(i -> createPublic())
        .toList();
  }

  public static List<Channel> createPrivateChannels() {
    return IntStream.range(0, 3)
        .mapToObj(i -> createPrivate())
        .toList();
  }
}
