package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelUpdateDto {
    private final UUID channelId;
    private final String name;
    private final String description;
}
