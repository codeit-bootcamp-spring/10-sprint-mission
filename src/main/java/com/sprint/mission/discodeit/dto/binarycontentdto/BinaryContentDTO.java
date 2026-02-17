package com.sprint.mission.discodeit.dto.binarycontentdto;

public record BinaryContentDTO(
        String contentType,
        byte[] file
) {}
