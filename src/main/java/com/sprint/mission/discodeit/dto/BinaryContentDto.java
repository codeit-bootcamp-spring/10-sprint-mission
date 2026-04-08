package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        String contentType,
        Long size,

        @JsonIgnore
        byte[] bytes
) {
    public BinaryContentDto(String fileName, String contentType, Long size, byte[] bytes) {
        this(null, fileName, contentType, size, bytes);
    }
}
