package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "lastActiveAt", target = "lastActiveAt")
  UserStatusResponse toResponse(UserStatus userStatus);
}