package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;

import java.util.UUID;

public record UserUpdateRequestDto(
        UUID targetUserId,
        String newUsername,
        String newEmail,
        String newPassword,
        BinaryContentRequestDto profileImage
) {
}
