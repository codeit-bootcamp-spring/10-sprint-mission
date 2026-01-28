package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateDTO(
        byte[] data,
        String contentType,
        String filename
) { }
