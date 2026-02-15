package com.sprint.mission.discodeit.dto.request.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BinaryContentCreateRequestDTO (
    @NotBlank
    String fileName,

    byte[] binaryContent,

    @NotNull
    BinaryContentType binaryContentType
) {

}