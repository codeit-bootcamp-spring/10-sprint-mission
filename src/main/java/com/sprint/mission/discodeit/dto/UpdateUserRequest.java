package com.sprint.mission.discodeit.dto;

public record UpdateUserRequest(
        String nickName,
        String userName,
        String email,
        String phoneNumber
) {
}
