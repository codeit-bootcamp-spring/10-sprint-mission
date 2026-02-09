package com.sprint.mission.discodeit.mapper.userstatus;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserStatusResponseMapper {
    UserStatusResponseDto toDto(UserStatus userStatus);
}
