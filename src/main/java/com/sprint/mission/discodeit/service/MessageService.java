package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto createMessage(CreateMessageRequestDTO dto, List<CreateBinaryContentPayloadDTO> attachments);

    List<MessageDto> findAllByUserId(UUID userId);

    List<MessageDto> findAllByChannelId(UUID channelId);

    MessageDto findByMessageId(UUID messageId);

    MessageDto updateMessage(UUID messageId, UpdateMessageRequestDTO dto);

    void deleteMessage(UUID messageId);
}
