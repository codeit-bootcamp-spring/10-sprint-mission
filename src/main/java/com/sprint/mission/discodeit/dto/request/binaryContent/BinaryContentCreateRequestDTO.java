package com.sprint.mission.discodeit.dto.request.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinaryContentCreateRequestDTO {
    @NotBlank
    private String fileName;

    private byte[] binaryContent;

    @NotNull
    private BinaryContentType binaryContentType;
}
