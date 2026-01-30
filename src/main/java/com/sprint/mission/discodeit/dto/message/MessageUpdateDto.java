package com.sprint.mission.discodeit.dto.message;

import lombok.Getter;

import java.util.UUID;

@Getter
public class MessageUpdateDto {
    private UUID id;
    private UUID userId;
    private String text;
}
