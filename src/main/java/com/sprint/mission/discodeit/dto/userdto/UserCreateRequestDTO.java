package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;

public record UserCreateRequestDTO(
    String username,
    String email,
    String password,
    BinaryContentDTO binaryContentDTO
) {

}
