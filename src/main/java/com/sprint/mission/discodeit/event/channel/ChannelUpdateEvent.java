package com.sprint.mission.discodeit.event.channel;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChannelUpdateEvent {
    ChannelDto channel;
}
