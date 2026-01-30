package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

public record UserCreateRequestDTO(
        String userName,
        String email,
        String password,
        BinaryContentDTO binaryContentDTO
) {
}
