package com.sprint.mission.discodeit.generator;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.MessageFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestEntity {

  @Autowired
  private EntityManager em;

  public User generatorUser() {
    User user = UserFixture.createEntity();
    return persistAndFlush(user);
  }

  public Channel generatorPublicChannel() {
    Channel channel = ChannelFixture.createPublic();
    return persistAndFlush(channel);
  }

  public Channel generatorPrivateChannel() {
    Channel channel = ChannelFixture.createPrivate();
    return persistAndFlush(channel);
  }

  public Message generatorMessage() {
    User author = generatorUser();
    Channel channel = generatorPublicChannel();
    List<BinaryContent> attachments = List.of(generatorBinaryContent());
    Message message = MessageFixture.createEntity(author, channel, attachments);
    return persistAndFlush(message);
  }

  public BinaryContent generatorBinaryContent() {
    BinaryContent binaryContent = BinaryContentFixture.createEntity();
    return persistAndFlush(binaryContent);
  }

  private <T> T persistAndFlush(T entity) {
    em.persist(entity);
    em.flush();
    return entity;
  }
}
