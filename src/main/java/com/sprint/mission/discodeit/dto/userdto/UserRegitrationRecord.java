package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.BinaryContentRecord;

public record UserRegitrationRecord(
        String userName,
        String email,
        String password,
        BinaryContentRecord binaryContentRecord
) {
}
