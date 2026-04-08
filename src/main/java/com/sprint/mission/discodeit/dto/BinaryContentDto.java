package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

/**
 * DTO for {@link BinaryContent}
 */
@Builder
public record BinaryContentDto(UUID id, LocalDateTime createdAt, String fileName, Long size,
                               String contentType, byte[] bytes)
    implements Serializable {

}
