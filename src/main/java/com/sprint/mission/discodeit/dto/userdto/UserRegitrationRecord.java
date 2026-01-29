package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

public record UserRegitrationRecord(
        String userName,
        String email,
        String password,
        BinaryContentDTO binaryContentDTO
) {
}
