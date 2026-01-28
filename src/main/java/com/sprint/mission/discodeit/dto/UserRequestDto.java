package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserRequestDto(String username,
                             String email,
                             String password,
                             BinaryContentRequestDto profileImage)
{

}
