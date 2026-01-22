package com.sprint.mission.discodeit.dto;

public record CreateUserRequest(
        String nickName,
        String userName,
        String email,
        String phoneNumber
) {
}
