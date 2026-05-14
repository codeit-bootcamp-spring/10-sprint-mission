package com.sprint.mission.discodeit.dto.binarycontentdto;

public record BinaryContentCreateRequestDTO(
        String contentType,
        byte[] file
) {}
