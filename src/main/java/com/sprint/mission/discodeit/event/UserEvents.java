package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public final class UserEvents {
    private UserEvents() {}
    public record Created(UserDto.Response user) {}
    public record Updated(UserDto.Response user) {}
    public record Deleted(UserDto.Response user) {}
    public record RoleUpdated(UUID userId, Role oldRole, Role newRole) {}
    public record OnlineStatusChanged(UUID userId, boolean online) {}
}
