package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;

import java.util.List;
import java.util.UUID;

public record MessageCreateDTO(
        String msg,
        UUID channelId,
        UUID userId,
        List<BinaryContentDTO> attachments
) {}
