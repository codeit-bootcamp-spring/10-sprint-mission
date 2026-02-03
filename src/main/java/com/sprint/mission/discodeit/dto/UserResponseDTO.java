package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public record UserResponseDTO(
    UserStatus userStatus,
    UUID profileId,
    String nickName,
    String userName,
    String email,
    String phoneNumber
) {
}
