package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    MessageDto create(MessageCreateRequest messageCreateRequest,
                      Optional<List<BinaryContentCreateRequest>> binaryContentCreateRequestDTO);
    MessageDto find(UUID messageId);
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);
    MessageDto update(UUID messageId, MessageUpdateRequest messageUpdateRequest);
    void delete(UUID messageId);
}
