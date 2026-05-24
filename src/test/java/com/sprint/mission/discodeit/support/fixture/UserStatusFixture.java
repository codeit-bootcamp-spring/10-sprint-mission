package com.sprint.mission.discodeit.support.fixture;

import static org.instancio.Select.field;

import com.sprint.mission.discodeit.userstatus.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import java.time.Duration;
import java.time.Instant;
import org.instancio.Instancio;

public final class UserStatusFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static UserStatus createOnline() {
    return baseFixture.baseUpdatableEntity(UserStatus.class)
        .set(field(UserStatus::getLastActiveAt), Instant.now())
        .create();
  }

  public static UserStatusUpdateRequest createUpdate() {
    return Instancio.create(UserStatusUpdateRequest.class);
  }

  public static UserStatus createOffline() {
    Instant pastTime = Instant.now().minus(Duration.ofDays(1));
    return baseFixture.baseUpdatableEntity(UserStatus.class)
        .set(field(UserStatus::getLastActiveAt), pastTime)
        .create();
  }
}
