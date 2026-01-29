package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BinaryContentCreateRequestDTO {
    @NotBlank
    private String fileName;

    private byte[] binaryContent;

    @NotNull
    private BinaryContentType binaryContentType;
}
