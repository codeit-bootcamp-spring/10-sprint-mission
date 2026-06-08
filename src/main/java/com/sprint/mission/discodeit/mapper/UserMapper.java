package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(target = "online", expression = "java(isOnline(entity, jwtRegistry))")
  UserDto toDto(User entity, @Context JwtRegistry jwtRegistry);

  default boolean isOnline(User user, JwtRegistry jwtRegistry) {
    return jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "profile", ignore = true)
  User toEntity(UserCreateRequest request);
}
