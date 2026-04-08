package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;
import net.datafaker.Faker;

public final class MessageFixture {

  private static final Faker faker = new Faker();

  public static MessageCreateRequest createRequest(UUID channelId, UUID authorId) {
    return new MessageCreateRequest(getContent(), channelId, authorId);
  }

  public static Message createEntity(User author, Channel channel,
      List<BinaryContent> attachments) {
    return Message.builder()
        .content(getContent())
        .channel(channel)
        .author(author)
        .attachments(attachments)
        .build();
  }

  public static Message createEntity() {
    return createEntity(UserFixture.createEntity(), ChannelFixture.createPublic(),
        List.of(BinaryContentFixture.createEntity()));
  }

  private static String getContent() {
    return faker.text().text();
  }

}
