package com.sprint.mission.discodeit.dto;

public record UserCreateRequestDto(String username,
                                   String email,
                                   String password,
                                   BinaryContentRequestDto profileImage)
{

}
