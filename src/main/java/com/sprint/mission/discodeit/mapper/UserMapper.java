package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.UserSessionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {BinaryContentMapper.class, UserSessionService.class})
public interface UserMapper {

  @Mapping(target = "online", source = "id", qualifiedByName = "isOnline")
  UserDto toDto(User user);
}
