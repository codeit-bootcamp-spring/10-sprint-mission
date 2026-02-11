package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest request);

    MessageResponse findById(UUID messageId);

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse update(UUID messageId, MessageUpdateRequest request);

    // 첨부파일 함께 삭제
    void delete(UUID messageId);
}
