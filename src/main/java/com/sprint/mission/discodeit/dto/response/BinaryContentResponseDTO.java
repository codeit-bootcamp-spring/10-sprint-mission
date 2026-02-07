package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record BinaryContentResponseDTO(
   UUID binaryContentId,
   byte[] content,
   String contentType
) {}
