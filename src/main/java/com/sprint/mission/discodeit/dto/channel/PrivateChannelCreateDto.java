package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDto(
        @NotEmpty
        List<UUID> memberIds
) {
}
