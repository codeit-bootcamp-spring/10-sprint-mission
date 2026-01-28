package com.sprint.mission.discodeit.DTO.UserDTO;

import com.sprint.mission.discodeit.DTO.BinaryContentRecord;

import java.util.UUID;

public record UserUpdateDTO(
        UUID id,
        String name,
        String email,
        String password,
        BinaryContentRecord newProfile

) {
}
