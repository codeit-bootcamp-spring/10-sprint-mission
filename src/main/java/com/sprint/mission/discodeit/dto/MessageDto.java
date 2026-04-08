package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link Message}
 */
public record MessageDto(UUID id, LocalDateTime createdAt, LocalDateTime updatedAt,
                         String content, UUID channelId,
                         UUID authorId, Set<BinaryContentDto> attachments)
    implements Serializable {

}
