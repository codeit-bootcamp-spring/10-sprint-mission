package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.MessageType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageCreateRequestDTO {
    private UUID authorId;
    private UUID channelId;
    private String message;
    private MessageType messageType;
    private List<BinaryContentCreateRequestDTO> binaryContentCreateRequestDTOList;
}
