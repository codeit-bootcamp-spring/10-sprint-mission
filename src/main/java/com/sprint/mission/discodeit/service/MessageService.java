package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import java.util.UUID;
import java.util.List;


public interface MessageService {
    Message create(String content, UUID userId, UUID channelId);

    Message findById(UUID id);

    List<Message> findAll();

    Message update(UUID id, String content);

    void delete(UUID id);

    List<Message> findAllByChannelId(UUID channelId, UUID userID); // 특정 채널의 메시지 목록 조회
}