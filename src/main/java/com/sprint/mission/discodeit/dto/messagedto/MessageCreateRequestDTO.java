package com.sprint.mission.discodeit.dto.messagedto;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequestDTO(
        String content,
        UUID channelID,
        UUID authorID,
        List<BinaryContentDTO> binaryContents
) {
}
