package com.sprint.mission.discodeit.binarycontent.dto;

import java.util.UUID;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
