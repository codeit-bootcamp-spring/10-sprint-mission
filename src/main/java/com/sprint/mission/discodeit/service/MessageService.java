package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageDto createMessage(
            UUID requestId,
            MessageCreateRequest request,
            List<BinaryContentRequest> attachments
    );

    PageResponse<MessageDto> findAllMessagesByChannelId(
            UUID channelId,
            UUID cursor,
            Pageable pageable
    );

    MessageDto updateMessage(UUID messageId, MessageUpdateRequest request);

    void deleteMessage(UUID messageId);
}
