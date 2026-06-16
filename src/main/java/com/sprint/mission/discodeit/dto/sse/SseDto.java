package com.sprint.mission.discodeit.dto.sse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class SseDto {
    private final UUID receiverId;
    private final String eventName;
    private final Object dto;
}
