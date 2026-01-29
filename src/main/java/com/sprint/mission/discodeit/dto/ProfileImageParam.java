package com.sprint.mission.discodeit.dto;

public record ProfileImageParam(
        byte[] data,
        String contentType
) {}