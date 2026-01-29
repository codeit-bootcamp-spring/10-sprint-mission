package com.sprint.mission.discodeit.dto;

public record BinaryContentDTO(
        String contentType,
        byte[] file
) {}
