package com.sprint.mission.discodeit.DTO;

public record UserRegitrationRecord(
        String userName,
        String email,
        String password,
        BinaryContentRecord binaryContentRecord
) {
}
