package com.sprint.mission.discodeit.channel.infra.repository.qdsl.dto;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.time.Instant;
import java.util.List;

public record ChannelDetailDto(
    Channel channel,
    Instant lastMessageAt,
    List<User> participants
) {

}
