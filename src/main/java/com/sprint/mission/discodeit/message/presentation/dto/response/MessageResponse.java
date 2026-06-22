package com.sprint.mission.discodeit.message.presentation.dto.response;

import com.sprint.mission.discodeit.binarycontent.presentation.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.user.presentation.dto.response.UserResponse;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;

/**
 * DTO for {@link Message}
 */
@Builder
public record MessageResponse(UUID id,
                              Instant createdAt,
                              Instant updatedAt,
                              String content,
                              UUID channelId,
                              UserResponse author,
                              Collection<BinaryContentDto> attachments)
    implements Serializable {

}
