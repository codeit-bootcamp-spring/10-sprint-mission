package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDTO create(MessageCreateRequestDTO req);
    MessageResponseDTO find(UUID messageId);
    List<MessageResponseDTO> findallByChannelId(UUID channelId);
    MessageResponseDTO update(MessageUpdateRequestDto req);
    void delete(UUID messageId);
}
