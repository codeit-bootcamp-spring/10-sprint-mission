package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.List;

public record ChannelDetailResponse(
    Channel channel,
    Instant lastMessageAt,
    List<User> participants
) {

}
