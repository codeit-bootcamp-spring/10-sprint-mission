package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.config.GlobalMapperConfig;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = GlobalMapperConfig.class, uses = BinaryContentMapper.class)
public interface UserMapper extends BaseMapper<User, UserDto> {

  User toEntityFrom(UserCreateRequest request, UserStatus status,
      @Context Optional<BinaryContent> profile);

  @AfterMapping
  default void linkStatus(@MappingTarget User user) {
    UserStatus status = user.getStatus();
    if (status != null) {
      status.setUser(user);
    }
  }

  User partialUpdate(UserUpdateRequest request, @Context Optional<BinaryContent> profile,
      @MappingTarget User user);

  @AfterMapping
  default void updateProfile(@MappingTarget User user, @Context Optional<BinaryContent> profile) {
    if (profile.isEmpty()) {
      return;
    }
    user.setProfile(profile.get());
  }
}
