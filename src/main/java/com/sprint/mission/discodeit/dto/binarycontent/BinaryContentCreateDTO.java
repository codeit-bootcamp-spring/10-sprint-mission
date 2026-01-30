package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateDTO(
        byte[] data,
        String contentType,
        String filename
) { }
