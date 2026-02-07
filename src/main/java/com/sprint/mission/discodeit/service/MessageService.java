package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequestDTO;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDTO create(MessageCreateRequestDTO messageCreateRequestDTO);
    MessageResponseDTO find(UUID messageId);
    List<MessageResponseDTO> findAllByChannelId(UUID channelId);
    MessageResponseDTO update(UUID messageId, MessageUpdateRequestDTO messageUpdateRequestDTO);
    void delete(UUID messageId);
}
