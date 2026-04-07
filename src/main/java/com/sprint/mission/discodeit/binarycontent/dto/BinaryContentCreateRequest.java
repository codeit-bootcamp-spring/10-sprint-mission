package com.sprint.mission.discodeit.binarycontent.dto;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
