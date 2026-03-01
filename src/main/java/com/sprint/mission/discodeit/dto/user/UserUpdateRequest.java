package com.sprint.mission.discodeit.dto.user;

// 유저 정보 수정 시 필요한 데이터
public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword
) {

}
