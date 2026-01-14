package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import java.util.List;


public interface MessageService {
    void create(Message message);

    Message readById(UUID id);

    List<Message> readAll();

    List<Message> readAllByChannelId(UUID channelId, UUID userID); // 특정 채널의 메시지 조회

    void update(Message message);

    void delete(UUID id);
}