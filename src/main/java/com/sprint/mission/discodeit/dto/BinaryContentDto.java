package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class BinaryContentDto {
    // =================================================================
    // 1. 파일 생성 요청 (Create)
    // =================================================================
    public record CreateRequest(
            @NotBlank
            String fileName,

            @NotBlank
            String contentType,

            @NotBlank
            byte[] content
    ) {}

}
