package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentProfileRequest;

// 유저 정보 수정 시 필요한 데이터
public record UserUpdateRequest(
        String name,
        String nickname,
        String email,
        BinaryContentProfileRequest profileImage
) {
}
