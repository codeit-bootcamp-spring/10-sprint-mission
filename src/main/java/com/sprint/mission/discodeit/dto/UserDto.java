package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.UUID;

public class UserDto {
    public record CreateRequest(
            String username,
            String email,
            String password,
            UUID profileId
    ) {}
    
    public record UpdateRequest(
            UUID id,
            String username,
            String email,
            String password,
            UUID profileId
    ) {}
    
    public record Response(
            UUID id,
            String username,
            String email,
            UUID profileId,
            boolean isOnline
    ) {}
}
