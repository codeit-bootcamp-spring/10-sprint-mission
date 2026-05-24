package com.sprint.mission.discodeit.support.generator;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.support.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.support.fixture.ChannelFixture;
import com.sprint.mission.discodeit.support.fixture.MessageFixture;
import com.sprint.mission.discodeit.support.fixture.ReadStatusFixture;
import com.sprint.mission.discodeit.support.fixture.UserFixture;
import jakarta.persistence.EntityManager;
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
    Message message = MessageFixture.createEntity();
    return persistAndFlush(message);
  }

  public BinaryContent generatorBinaryContent() {
    BinaryContent binaryContent = BinaryContentFixture.createEntity();
    return persistAndFlush(binaryContent);
  }

  public ReadStatus generatorReadStatus() {
    ReadStatus status = ReadStatusFixture.createEntity();
    return persistAndFlush(status);
  }

  private <T> T persistAndFlush(T entity) {
    em.persist(entity);
    em.flush();
    return entity;
  }
}
