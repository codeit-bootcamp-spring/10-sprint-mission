package com.sprint.mission.discodeit.dto.request;

public record AttachmentCreateRequestDTO(
    byte[] bytes,
    String contentType
) {}
