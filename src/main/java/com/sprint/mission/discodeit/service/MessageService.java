package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // CRUD
    Message create(CreateRequest Request);
    MessageResponse find(UUID messageID);
    List<MessageResponse> findAllByUserID();
    MessageResponse update(MessageUpdateRequest request);
    default void update() {}
    void deleteMessage(UUID messageID);

    // 도메인 별 메시지 조회
    List<MessageResponse> findMessagesByChannel(UUID channelID);
    List<MessageResponse> findMessagesByUser(UUID userID);
}
