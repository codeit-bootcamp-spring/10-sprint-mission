package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateDto {
    private final UUID senderId;
    private final UUID channelId;
    private final String content;
    private final List<BinaryContentDto> attachments;
}
