package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentProfileRequest;

// 유저 생성 시 필요한 데이터
public record UserCreateRequest(
        String name,
        String nickname,
        String email,
        String password,
        BinaryContentProfileRequest profileImage
) {
}
