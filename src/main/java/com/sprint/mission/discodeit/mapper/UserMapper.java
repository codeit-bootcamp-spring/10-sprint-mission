package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Mapping(target = "online", expression = "java(user.getUserStatus() != null && user.getUserStatus().isOnline())")
  public abstract UserDto toDto(User user);
}
