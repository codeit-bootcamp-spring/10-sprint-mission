package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // CRUD
    MessageResponse create(MessageCreateRequest Request);
    MessageResponse find(UUID messageID);
    List<MessageResponse> findAllByUserID(UUID userID);
    List<MessageResponse> findAllByChannelID(UUID channelID);
    List<MessageResponse> findAll();
    MessageResponse update(MessageUpdateRequest request);
    default void update() {}
    void deleteMessage(UUID messageID);

    // 도메인 별 메시지 조회
    List<MessageResponse> findMessagesByChannel(UUID channelID);
    List<MessageResponse> findMessagesByUser(UUID userID);
}
