package com.sprint.mission.discodeit.dto;

import lombok.Builder;

import java.util.UUID;

public class UserDto {
    public record CreateRequest(
            String username,
            String email,
            String password,
            BinaryContentDto profileImage
    ) {}

    public record BinaryContentDto (
            byte[] data,
            String fileType,
            String fileName
    ) {}

    @Builder
    public record Response(
        UUID id,
        String username,
        String email,
        boolean isOnline,
        UUID profileImageId
    ) {}

    public record UpdateRequest (
        UUID id,
        String username,
        String email,
        String password,
        BinaryContentDto profileImage
    ) {}
}
