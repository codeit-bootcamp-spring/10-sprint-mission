package com.sprint.mission.discodeit.dto;

public record JwtDto(
    UserDto.Response userDto,
    String accessToken
) {
}
