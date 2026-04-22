package com.sprint.mission.discodeit.mapper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.BinaryContentFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.fixture.UserStatusFixture;
import com.sprint.mission.discodeit.mapper.factory.MapperContainer;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

  private final UserMapper userMapper = MapperContainer.get(UserMapper.class);
  private User emptyUser;

  @BeforeEach
  void setup() {
    emptyUser = User.builder().build();
  }

  @Test
  @DisplayName(""
      + "@Mapping(target = \"status\", qualifiedByName = \"toEntity\")\n"
      + "User toEntityFrom(UserCreateRequest request, BinaryContent profile);")
  void toEntityFrom() {
    UserCreateRequest request = UserFixture.createRequest();
    Optional<BinaryContent> profile = Optional.of(BinaryContentFixture.createEntity());
    UserStatus status = UserStatusFixture.createOnline();
    User user = userMapper.toEntityFrom(request, status, profile);
    notEqualsTo(user, emptyUser);
  }

  @Test
  @DisplayName("User partialUpdate(UserUpdateRequest request, BinaryContent profile, @MappingTarget User user);")
  void partialUpdate() {
    UserUpdateRequest request = UserFixture.createUpdateRequest();
    Optional<BinaryContent> profile = Optional.of(BinaryContentFixture.createEntity());
    User user = User.builder().build();
    userMapper.partialUpdate(request, profile, user);
    notEqualsTo(user, emptyUser);
  }

  private static void notEqualsTo(User user, User emptyUser) {
    assertAll(
        () -> assertNotEquals(user.getUsername(), emptyUser.getUsername()),
        () -> assertNotEquals(user.getEmail(), emptyUser.getEmail()),
        () -> assertNotEquals(user.getPassword(), emptyUser.getPassword())
    );
  }

}
