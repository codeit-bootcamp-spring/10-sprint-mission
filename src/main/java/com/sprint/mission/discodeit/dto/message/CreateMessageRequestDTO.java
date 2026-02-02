package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequestDTO;

import java.util.List;
import java.util.UUID;

public record CreateMessageRequestDTO(
        String content,
        UUID sentUserId,
        UUID sentChannelId,
        List<CreateBinaryContentRequestDTO> attachmentDTOs
) { }