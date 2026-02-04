package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;

import java.util.UUID;

public record UserUpdateDTO(
        UUID userId,
        String name,
        BinaryContentDTO profileImage
) {}
