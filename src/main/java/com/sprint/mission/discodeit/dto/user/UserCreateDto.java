package com.sprint.mission.discodeit.dto.user;

public record UserCreateDto(
        String email,
        String userName,
        String nickName,
        String password,
        String birthday,
        byte[] binaryContent
) {
    public UserCreateDto {
        if (binaryContent != null) {
            binaryContent = binaryContent.clone();
        }
    }
}
