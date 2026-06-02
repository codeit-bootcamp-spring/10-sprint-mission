package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

    @Autowired
    protected JwtRegistry jwtRegistry;

    @Mapping(target = "online", expression = "java(jwtRegistry.hasActiveJwtInformationByUserId(user.getId()))")
    public abstract UserDto.Response toResponse(User user);
}
