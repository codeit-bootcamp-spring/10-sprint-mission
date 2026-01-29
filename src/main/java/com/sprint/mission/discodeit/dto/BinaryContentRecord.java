package com.sprint.mission.discodeit.dto;

public record BinaryContentRecord(
        String contentType,
        byte[] file
) {}
