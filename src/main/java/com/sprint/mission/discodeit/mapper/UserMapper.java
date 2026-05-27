package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Mapping(target = "profile", source = "user.profile")
    @Mapping(target = "online", expression = "java(isOnline(user))")
    public abstract UserDTO toDTO(User user);

    protected boolean isOnline(User user){
        return sessionRegistry.getAllPrincipals()
                .stream()
                .map(p -> (DiscodeitUserDetails) p)
                .anyMatch(p -> p.getUserDTO().id().equals(user.getId()));
    }
}
