package com.sprint.mission.discodeit.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class UserDto {
    private UserDto() {}

    public record LoginRequest(String username, String password) {}
    public record createRequest(String accountId, String password, String name, String mail) {}
    public record updateRequest(String accountId, String password, String name, String mail) {}
    public record response(UUID uuid, Instant createdAt, Instant updatedAt,
                           String accountId, String name, String mail,
                           UUID profileId, boolean isOnline,
                           List<UUID> joinedChannels,
                           List<UUID> messageHistory) {}
}
