package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.MessageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageCreateRequestDTO {
    @NotNull
    private UUID authorId;

    @NotNull
    private UUID channelId;

    @NotBlank
    private String message;

    @NotNull
    private MessageType messageType;

    @Valid
    private List<BinaryContentCreateRequestDTO> binaryContentCreateRequestDTOList;
}
