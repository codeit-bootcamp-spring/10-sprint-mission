package com.sprint.mission.discodeit.mapper.user;

import com.sprint.mission.discodeit.dto.user.LoginResponseDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginResponseMapper {
    LoginResponseDto toDto(User targetUser);
}
