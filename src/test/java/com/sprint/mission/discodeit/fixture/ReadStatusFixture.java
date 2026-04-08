package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;

public final class ReadStatusFixture {

  private ReadStatusFixture() {
  }

  public static ReadStatus createEntity(User user, Channel channel) {
    return new ReadStatus(user, channel, Instant.now());
  }

  public static ReadStatus createEntity() {
    return new ReadStatus(UserFixture.createEntity(), ChannelFixture.createPrivate(),
        Instant.now());
  }
}
