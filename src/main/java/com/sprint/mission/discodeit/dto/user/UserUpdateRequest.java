package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;

// 정보 수정 시 변경할 정보
public record UserUpdateRequest (
        String username,
        String email,
        String password,
        BinaryContentRequest profileImage  // 프로필 사진을 수정하지 않으면 null
) {}