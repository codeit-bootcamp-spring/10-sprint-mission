package com.sprint.mission.discodeit.dto.userstatus.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserStatusUpdateCommand(UUID userId, LocalDateTime datetime) {
}
