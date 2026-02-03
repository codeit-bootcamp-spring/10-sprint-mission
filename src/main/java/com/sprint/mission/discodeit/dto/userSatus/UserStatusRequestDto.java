package com.sprint.mission.discodeit.dto.userSatus;

import com.sprint.mission.discodeit.dto.user.UserRequestDto;

import java.time.Instant;
import java.util.UUID;

public record UserStatusRequestDto(
//        Instant createdAt,//최초 로그인 시간
//        Instant updatedAt,
        //
        UUID userId,
        UserRequestDto userRequestDto
) {
}
