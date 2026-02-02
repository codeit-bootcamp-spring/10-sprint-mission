package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import java.util.UUID;
import java.util.List;


public interface MessageService {
    MessageResponse create (MessageCreateRequest request);

    MessageResponse getMessageById(UUID id);

    List<MessageResponse> getAllByChannelId(UUID channelId, UUID userId); // 특정 채널의 메시지 목록 조회

    MessageResponse update(UUID id, MessageUpdateRequest request);

    void deleteById(UUID id);

    MessageResponse togglePin(UUID id); // 메시지 고정
}