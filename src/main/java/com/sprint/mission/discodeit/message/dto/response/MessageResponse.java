package com.sprint.mission.discodeit.message.dto.response;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.user.dto.response.UserResponse;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link Message}
 */
public record MessageResponse(UUID id,
                              Instant createdAt,
                              Instant updatedAt,
                              String content,
                              UUID channelId,
                              UserResponse author,
                              Set<BinaryContentDto> attachments)
    implements Serializable {

}
