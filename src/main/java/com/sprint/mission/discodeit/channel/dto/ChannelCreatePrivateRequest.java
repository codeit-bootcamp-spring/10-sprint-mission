package com.sprint.mission.discodeit.channel.dto;

import java.util.List;
import java.util.UUID;

public record ChannelCreatePrivateRequest(
    List<UUID> participantIds

) {

}
