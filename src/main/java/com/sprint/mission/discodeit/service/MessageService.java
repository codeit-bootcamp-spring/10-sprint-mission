package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponseDTO create(List<byte[]> attachments, MessageCreateRequestDTO req);

    MessageResponseDTO find(UUID messageId);

    List<MessageResponseDTO> findAllByChannelId(UUID channelId);

    MessageResponseDTO update(UUID messageId, MessageUpdateRequestDto req);

    void delete(UUID messageId);
}
