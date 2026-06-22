package com.sprint.mission.discodeit.binarycontent.presentation.dto.response;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;

/**
 * DTO for {@link BinaryContent}
 */
@Builder
public record BinaryContentDto(UUID id,
                               String fileName,
                               Long size,
                               String contentType,
                               BinaryContentStatus status)
    implements Serializable {

}
