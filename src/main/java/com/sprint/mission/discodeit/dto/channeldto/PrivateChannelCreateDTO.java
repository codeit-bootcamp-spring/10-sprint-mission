package com.sprint.mission.discodeit.dto.channeldto;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDTO(
        List<UUID> users
) {
}
