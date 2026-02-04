package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.IsPrivate;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateDto(
        UUID ownerId,
        List<UUID> users // 방장 포함 채널 참여자
) {
}
