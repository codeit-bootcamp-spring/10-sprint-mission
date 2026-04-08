package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link Channel}
 */
public record ChannelDto(UUID id, ChannelType type, String name, String description,
                         List<UserDto> participants, LocalDateTime lastMessageAt)
    implements Serializable {

}
