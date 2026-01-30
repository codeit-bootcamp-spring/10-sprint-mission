package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.content.BinaryContentDto;

import java.util.UUID;

// 유저 정보 수정 시 필요한 데이터
public record UserUpdateRequest(
        String name,
        String nickname,
        String email,
        BinaryContentDto profileImage
) {
}
