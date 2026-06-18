package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.enums.ChannelType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChannelCreateResponse {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private ChannelType type;
    private String name;
    private String description;
}
