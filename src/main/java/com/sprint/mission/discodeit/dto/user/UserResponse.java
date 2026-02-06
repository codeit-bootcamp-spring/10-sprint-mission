package com.sprint.mission.discodeit.dto.user;

import java.time.Instant;
import java.util.UUID;

// 사용자 정보를 조회할 때 반환
public record UserResponse (
        UUID id,
        String username,
        String email,
        // password는 반환하지 않음 (!보안 이슈!)
        UUID profileImageId,
        boolean isOnline // 접속 상태 (true: 온라인, false: 오프라인)
) {}
