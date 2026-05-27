package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {

  @Autowired
  protected SessionRegistry sessionRegistry;

  @Mapping(target = "online", expression = "java(isUserOnline(user))")
  public abstract UserDto toDto(User user);

  protected boolean isUserOnline(User user) {
    if (sessionRegistry == null) return false;
    UserDto dummyDto = new UserDto(user.getId(), null, null, null, null, null);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(dummyDto, null);
    return !sessionRegistry.getAllSessions(principal, false).isEmpty();
  }
}
