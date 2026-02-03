package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDTO;

import java.util.List;
import java.util.UUID;

public record UserDTO(
        UUID userId,
        String name,
        String email,
        UUID profileId,
        List<UUID> channelList,
        List<UUID> messageList,
        UserStatusDTO userStatus
) {}
