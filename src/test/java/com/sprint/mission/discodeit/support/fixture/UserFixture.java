package com.sprint.mission.discodeit.support.fixture;

import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.entity.User;
import java.util.List;
import org.instancio.Instancio;

public final class UserFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static UserCreateRequest createRequest() {
    return Instancio.create(UserCreateRequest.class);
  }

  public static User createEntity() {
    return baseFixture.baseUpdatableEntity(User.class).create();
  }

  public static List<User> createEntities() {
    return baseFixture.baseEntities(User.class).create();
  }

  public static UserUpdateRequest createUpdateRequest() {
    return Instancio.create(UserUpdateRequest.class);
  }
}
