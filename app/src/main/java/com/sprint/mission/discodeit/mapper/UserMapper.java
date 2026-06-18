package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  @Autowired
  private JwtRegistry jwtRegistry;

  @Mapping(target = "online", source = "user", qualifiedByName = "isOnline")
  public abstract UserDto toDto(User user);

  @Named("isOnline")
  boolean isOnline(User user) {
    return jwtRegistry.hasActiveJwtInformationByUserId(user.getId());
  }
}
