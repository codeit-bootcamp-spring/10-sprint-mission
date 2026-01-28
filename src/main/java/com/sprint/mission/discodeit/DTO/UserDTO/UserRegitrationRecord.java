package com.sprint.mission.discodeit.DTO.UserDTO;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;

public record UserRegitrationRecord(
        String userName,
        String email,
        String password,
        BinaryContentRecord binaryContentRecord
) {
}
