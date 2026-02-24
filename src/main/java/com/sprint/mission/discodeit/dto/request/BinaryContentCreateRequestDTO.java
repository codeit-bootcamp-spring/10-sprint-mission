package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequestDTO(
    String fileName,
    byte[] bytes,
    String contentType
) {}
