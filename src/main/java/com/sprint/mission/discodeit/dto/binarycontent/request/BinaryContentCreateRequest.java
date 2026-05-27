package com.sprint.mission.discodeit.dto.binarycontent.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "첨부파일 생성 정보")
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {}

