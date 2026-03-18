package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId,
        Instant lastActiveAt //사용자가 마지막으로 활동한 시간. create인데 이게 필요한가?
) {}
