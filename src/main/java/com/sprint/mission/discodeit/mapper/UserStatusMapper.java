package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class UserStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  public abstract UserStatusDto toDto(UserStatus userStatus);
}
