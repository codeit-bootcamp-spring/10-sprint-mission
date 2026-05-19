package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

	@Mapping(target = "userId", source = "user.id")
	UserStatusDto toDto(UserStatus userStatus);
}
