package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.sse.SseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ChannelUpdatedEvent {
    private final List<SseDto> dtos;
}
