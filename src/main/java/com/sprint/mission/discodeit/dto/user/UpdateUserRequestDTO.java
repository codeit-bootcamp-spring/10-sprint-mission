package com.sprint.mission.discodeit.dto.user;

// 비어있으면 해당 필드는 업데이트 안 함
public record UpdateUserRequestDTO(
        String newUsername,
        String newEmail,
        String newPassword
) { }
