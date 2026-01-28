package com.sprint.mission.discodeit.dto.user;

public record UserCreateRequestDto(String username,
                                   String email,
                                   String password,
                                   BinaryContentRequestDto profileImage)
{

}
