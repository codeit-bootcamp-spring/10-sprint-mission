package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.SendMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDTO createMessage(SendMessageRequestDTO dto);

    List<MessageResponseDTO> findAllByUserId(UUID userId);

    List<MessageResponseDTO> findAllByChannelId(UUID channelId);

    MessageResponseDTO findByMessageId(UUID messageId);

    MessageResponseDTO updateMessage(UpdateMessageRequestDTO dto);

    void deleteMessage(UUID messageId);
}
