package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusDTOMapper {

    @Mapping(target = "userId", source = "user.id")
    UserStatusDto toDto(UserStatus userStatus);
}
