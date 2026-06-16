package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.sse.SseDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserUpdatedEvent {
    private final List<SseDto> dtos;
}
