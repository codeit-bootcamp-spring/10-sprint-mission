package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.security.session.UserSessionManager;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public abstract class UserMapper {

    @Autowired
    protected UserSessionManager userSessionManager;

    @Mapping(target = "online", expression = "java(userSessionManager.isOnline(user.getId()))")
    public abstract UserDto toDto(User user);

    @Mapping(target = "online", expression = "java(onlineUserIds.contains(user.getId()))")
    public abstract UserDto toDto(User user, Set<UUID> onlineUserIds);
}
