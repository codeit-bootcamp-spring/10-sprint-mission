package com.sprint.mission.discodeit.dto.binarycontent;

public record CreateBinaryContentRequestDTO(
        byte[] data,
        String contentType,
        String filename
) { }
