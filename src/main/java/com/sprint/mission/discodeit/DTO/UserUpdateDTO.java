package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record UserUpdateDTO(
        UUID id,
        String name,
        String email,
        String password,
        BinaryContentRecord newProfile

) {
}
