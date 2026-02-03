package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateDTO(
        String fileName,
        String fileType,
        byte[] bytes
) {}
