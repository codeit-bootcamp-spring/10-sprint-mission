package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto createMessage(CreateMessageRequestDTO dto, List<CreateBinaryContentPayloadDTO> attachments);

    PageResponse<MessageDto> findAllByUserId(UUID userId, Instant cursor, Pageable pageable);

    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    MessageDto findByMessageId(UUID messageId);

    MessageDto updateMessage(UUID messageId, UpdateMessageRequestDTO dto);

    void deleteMessage(UUID messageId);
}
