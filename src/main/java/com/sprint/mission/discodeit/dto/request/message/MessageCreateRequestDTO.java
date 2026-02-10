package com.sprint.mission.discodeit.dto.request.message;

import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.entity.MessageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MessageCreateRequestDTO (
    @NotNull
    UUID authorId,

    @NotNull
    UUID channelId,

    @NotBlank
    String message,

    @NotNull
    MessageType messageType,

    @Valid
    List<BinaryContentCreateRequestDTO> binaryContentCreateRequestDTOList
) {

}
