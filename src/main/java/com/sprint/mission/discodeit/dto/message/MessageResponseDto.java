package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponseDto {
    private UUID id;
    private UUID userId;
    private UUID channelId;
    private String text;
}
