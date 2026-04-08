package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateMessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UpdateMessageRequestDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(CreateMessageRequestDto request, List<BinaryContentDto> attachments);
    MessageDto find(UUID messageId);
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,Pageable pageable);
    MessageDto update(UUID messageId, UpdateMessageRequestDto request);
    void delete(UUID messageId);
}
