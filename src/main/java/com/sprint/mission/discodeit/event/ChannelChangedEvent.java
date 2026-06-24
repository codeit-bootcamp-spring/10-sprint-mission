package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.ChannelDto;

public record ChannelChangedEvent(String action, ChannelDto dto) {
}
