package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.common.mapper.BaseMapper;
import com.sprint.mission.discodeit.common.mapper.config.GlobalMapperConfig;
import com.sprint.mission.discodeit.user.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.userstatus.entity.UserStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(config = GlobalMapperConfig.class, uses = BinaryContentMapper.class)
public interface UserMapper extends BaseMapper<User, UserResponse> {


  @Mapping(target = "status", source = "status")
  User toEntityFrom(UserCreateRequest request, UserStatus status, BinaryContent profile);

  @AfterMapping
  default void linkStatus(@MappingTarget User user) {
    UserStatus status = user.getStatus();
    if (status != null) {
      status.setUser(user);
    }
  }

  @Mapping(target = "status", ignore = true)
  User partialUpdate(UserUpdateRequest request, BinaryContent profile, @MappingTarget User user);

  User partialUpdate(RoleUpdateRequest request, @MappingTarget User user);
}
