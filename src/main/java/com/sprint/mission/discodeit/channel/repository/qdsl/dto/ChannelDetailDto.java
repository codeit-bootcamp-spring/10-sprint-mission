package com.sprint.mission.discodeit.channel.repository.qdsl.dto;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.user.entity.User;
import java.time.Instant;
import java.util.List;

public record ChannelDetailDto(
    Channel channel,
    Instant lastMessageAt,
    List<User> participants
) {

}
