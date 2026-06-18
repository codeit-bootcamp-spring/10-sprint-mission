package com.sprint.mission.discodeit.event.channel;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelCreatedEvent {

  private final ChannelDto channel;
  private final Set<UUID> receiverIds;
}
