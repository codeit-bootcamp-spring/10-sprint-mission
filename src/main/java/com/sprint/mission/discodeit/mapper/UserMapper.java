package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

    @Autowired
    protected SessionRegistry sessionRegistry;

    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserDto toDto(User user);

    protected boolean isOnline(User user) {
        return sessionRegistry.getAllPrincipals().stream()
            .filter(DiscodeitUserDetails.class::isInstance)
            .map(DiscodeitUserDetails.class::cast)
            .filter(principal -> user.getId().equals(principal.getUserDto().id()))
            .anyMatch(principal -> !sessionRegistry.getAllSessions(principal, false).isEmpty());
    }
}
