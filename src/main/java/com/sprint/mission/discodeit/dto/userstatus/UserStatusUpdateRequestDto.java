package com.sprint.mission.discodeit.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequestDto(
        boolean isUserId,
        UUID Id, //위의 boolean이 true이면 유저값, false면 status 자체 객체값이다.
        Instant lastOnlineTime
){
}
