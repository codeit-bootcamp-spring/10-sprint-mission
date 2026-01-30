package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.StatusType;

import java.util.UUID;

public record UserInfoDto(

        String userName,
        UUID userId,
        StatusType status,
        String email,
        UUID profileId


) {
}
