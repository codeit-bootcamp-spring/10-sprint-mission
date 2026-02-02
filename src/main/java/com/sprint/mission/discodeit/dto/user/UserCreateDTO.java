package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

public record UserCreateDTO(
        String name,
        String email,
        String password,
        BinaryContentDTO profileImage
){}