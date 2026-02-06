package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;

public record UserCreateRequestDto(String username,
                                   String email,
                                   String password
)
{

}
