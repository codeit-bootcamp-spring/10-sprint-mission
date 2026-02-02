package com.sprint.mission.discodeit.dto.binarycontent;

public record CreateBinaryContentPayloadDTO(
        byte[] data,
        String contentType,
        String filename
) { }
