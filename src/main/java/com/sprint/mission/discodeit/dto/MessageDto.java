package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link Message}
 */
public record MessageDto(UUID id, Instant createdAt, Instant updatedAt,
                         String content, UUID channelId,
                         UserDto author, Set<BinaryContentDto> attachments)
    implements Serializable {

}
