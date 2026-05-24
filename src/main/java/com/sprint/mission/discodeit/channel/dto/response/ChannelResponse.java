package com.sprint.mission.discodeit.channel.dto.response;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.channel.entity.constant.ChannelType;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Channel}
 */
public record ChannelResponse(UUID id,
                              ChannelType type,
                              String name,
                              String description,
                              List<UserResponse> participants,
                              Instant lastMessageAt)
    implements Serializable {

}
