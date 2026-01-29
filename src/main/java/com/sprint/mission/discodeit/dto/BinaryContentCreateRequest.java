package com.sprint.mission.discodeit.dto;

public record BinaryContentCreateRequest(
        byte[] binaryContent
) {
    public BinaryContentCreateRequest {
        if (binaryContent != null) {
            binaryContent = binaryContent.clone();
        }
    }
}
