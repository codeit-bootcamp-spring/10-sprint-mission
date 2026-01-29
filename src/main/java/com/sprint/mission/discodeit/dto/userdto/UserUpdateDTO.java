package com.sprint.mission.discodeit.dto.userdto;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

import java.util.UUID;

public record UserUpdateDTO(
        UUID id,
        String name,
        String email,
        String password,
        BinaryContentDTO newProfile

) {
}
