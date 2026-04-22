package com.sprint.mission.discodeit.fixture;

import static org.instancio.Select.field;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;

public final class MessageFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static Message createEntity() {
    return baseFixture.baseUpdatableEntity(Message.class)
        .set(field(Message::getAttachments), BinaryContentFixture.createEntities())
        .create();
  }

  public static List<Message> createEntities() {
    return baseFixture.baseUpdatableEntities(Message.class)
        .set(field(Message::getAttachments), BinaryContentFixture.createEntities())
        .create();
  }
}
