package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.sse.SseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BinaryContentUpdatedEvent {
    private final List<SseDto> dtos;
}
