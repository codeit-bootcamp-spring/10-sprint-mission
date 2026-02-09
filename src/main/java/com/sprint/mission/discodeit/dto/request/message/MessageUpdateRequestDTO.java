package com.sprint.mission.discodeit.dto.request.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageUpdateRequestDTO {
    @Setter
    @NotNull
    private UUID id;

    @NotBlank
    private String message;
}
