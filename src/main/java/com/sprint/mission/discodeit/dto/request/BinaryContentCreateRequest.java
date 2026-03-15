package com.sprint.mission.discodeit.dto.request;

public record BinaryContentCreateRequest(
    String fileName,
    Long size,
    String contentType,
    byte[] bytes
) {

}
