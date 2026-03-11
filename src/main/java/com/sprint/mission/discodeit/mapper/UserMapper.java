package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

    @Mapping(target = "online", expression = "java(user.getUserStatus() != null && user.getUserStatus().isOnline())")
    UserDto toDto(User user);
}
