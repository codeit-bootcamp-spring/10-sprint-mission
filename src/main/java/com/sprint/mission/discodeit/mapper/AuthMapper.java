package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface AuthMapper {
    @Mapping(target = "profile", source = "user.profile")
    @Mapping(target = "online", constant = "false")
    UserDTO toResponse(User user);
}
