package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

  @Autowired
  private SessionRegistry getSessionRegistry;

  @Mapping(target = "online", source = "user", qualifiedByName = "isOnline")
  public abstract UserDto toDto(User user);

  @Named("isOnline")
  boolean isOnline(User user) {
    UserDto userDto = new UserDto(null, user.getUsername(), null, null, null, false);
    DiscodeitUserDetails targetDetails = new DiscodeitUserDetails(userDto, null);
    List<SessionInformation> sessions = getSessionRegistry.getAllSessions(targetDetails, false);
    return !sessions.isEmpty();
  }
}
