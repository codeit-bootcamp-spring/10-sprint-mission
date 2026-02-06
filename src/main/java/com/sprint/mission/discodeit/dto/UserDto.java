package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public class UserDto {
    public record BinaryContentDto(
            String fileName,
            byte[] data
    ) {}

    public record CreateRequest(
            String username,
            String email,
            String password,
            BinaryContentDto profileImage
    ) {}
    
    public record UpdateRequest(
            UUID id,
            String username,
            String email,
            String password,
            BinaryContentDto profileImage
    ) {}
    
    public record Response(
            UUID id,
            String username,
            String email,
            UUID profileId,
            boolean isOnline
    ) {}
}
