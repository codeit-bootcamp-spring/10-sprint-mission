package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.stream.IntStream;
import net.datafaker.Faker;

public final class UserFixture {

  private static final Faker faker = new Faker();

  private UserFixture() {
  }

  public static UserDto createUserDto() {
    return UserDto.builder()
        .username(getName())
        .email(getEmail())
        .build();
  }

  public static UserCreateRequest createRequest() {
    return new UserCreateRequest(getName(), getEmail(), getPassword());
  }

  public static User createEntity() {
    User user = User.builder()
        .username(getName())
        .email(getEmail())
        .password(getPassword())
        .profile(BinaryContentFixture.createEntity())
        .build();
    UserStatus status = UserStatusFixture.createOnline();
    user.setStatus(status);
    return user;
  }

  public static List<User> createEntities() {
    return IntStream.range(0, 3)
        .mapToObj(i -> createEntity())
        .toList();
  }

  public static UserUpdateRequest createUpdate() {
    return new UserUpdateRequest(getName(), getEmail(), getPassword());
  }

  private static String getPassword() {
    return faker.credentials().password();
  }

  private static String getEmail() {
    return faker.internet().emailAddress();
  }

  private static String getName() {
    return faker.name().name().replace(" ", "_");
  }
}
