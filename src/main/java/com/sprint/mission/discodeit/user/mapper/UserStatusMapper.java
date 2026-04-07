package com.sprint.mission.discodeit.user.mapper;

import com.sprint.mission.discodeit.user.dto.UserStatusDto;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

  @Mapping(source = "user.id", target = "userId")
  UserStatusDto toDto(UserStatus userStatus);
}