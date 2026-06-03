package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.auth.JwtDto;
import com.sprint.mission.discodeit.dto.response.auth.TokenDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {
    // JWT DTO 변환
    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "userDto", source = "userDto")
    JwtDto toJwtDto(String accessToken, UserDto userDto);

    // TokenDto 변환
    TokenDto toTokenDto(JwtDto jwtDto, String refreshToken);
}
