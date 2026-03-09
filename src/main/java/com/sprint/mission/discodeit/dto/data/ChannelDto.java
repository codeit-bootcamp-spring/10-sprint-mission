package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
    UUID id,
    ChannelType type,
    String name,
    String description,
    // 다른 클래스에서 계산 필요. UserDto 타입 반환
    List<UserDto> participants,
    Instant lastMessageAt
) {

}
