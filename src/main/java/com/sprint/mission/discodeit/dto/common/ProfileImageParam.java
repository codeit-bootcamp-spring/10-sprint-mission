package com.sprint.mission.discodeit.dto.common;

public record ProfileImageParam(
        byte[] data,
        String contentType
) {}