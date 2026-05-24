package com.sprint.mission.discodeit.support.fixture;

import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;

public final class ReadStatusFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static ReadStatus createEntity() {
    return baseFixture.baseUpdatableEntity(ReadStatus.class).create();
  }
}
