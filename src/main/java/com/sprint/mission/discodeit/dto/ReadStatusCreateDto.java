package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReadStatusCreateDto {
    private final UUID userId;
    private final UUID channelId;
}
