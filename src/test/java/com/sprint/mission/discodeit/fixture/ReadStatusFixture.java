package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.entity.ReadStatus;

public final class ReadStatusFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static ReadStatus createEntity() {
    return baseFixture.baseUpdatableEntity(ReadStatus.class).create();
  }
}
