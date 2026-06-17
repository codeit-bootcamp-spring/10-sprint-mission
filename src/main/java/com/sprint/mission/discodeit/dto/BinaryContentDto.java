package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
        UUID id,
        String fileName,
        String contentType,
        Long size,
        BinaryContentStatus status,

        @JsonIgnore
        byte[] bytes
) {
    // 응답 DTO는 BinaryContentMapper.toDto가 생성하며 실제 status를 채움
    public BinaryContentDto(String fileName, String contentType, Long size, byte[] bytes) {
        this(null, fileName, contentType, size, BinaryContentStatus.PROCESSING, bytes);
    }
}
