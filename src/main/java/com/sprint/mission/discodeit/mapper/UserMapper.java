package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

    @Autowired
    protected JwtRegistry jwtRegistry;

    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserDto toDto(User user);

    protected boolean isOnline(User user) {
        return jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
    }
}
