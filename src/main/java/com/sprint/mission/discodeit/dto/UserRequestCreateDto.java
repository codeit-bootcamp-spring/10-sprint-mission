package com.sprint.mission.discodeit.dto;

public record UserRequestCreateDto(String userName,
                                   String userEmail,
                                   ProfileImageParam profileImage) {
    public record ProfileImageParam(
            byte[] data,
            String contentType
    ) {}
}
