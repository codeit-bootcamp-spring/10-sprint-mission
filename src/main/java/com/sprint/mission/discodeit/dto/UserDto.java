package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class UserDto {
    public record LoginRequest(String username, String password) { }
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           String accountId, String name, String mail,
                           List<UUID> joinedChannels,
                           List<UUID> messageHistory) { }
}
