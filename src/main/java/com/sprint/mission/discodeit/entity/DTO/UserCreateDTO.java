package com.sprint.mission.discodeit.entity.DTO;

import com.sprint.mission.discodeit.entity.BinaryContent;

public record UserCreateDTO(
        String name,
        String email,
        String password,
        BinaryContent profile
){}