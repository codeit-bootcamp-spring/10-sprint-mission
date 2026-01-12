package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    void create(Message message);
    Message read(UUID id);
    List<Message> readAll();
    List<Message> readByChannel(UUID channelId); // 채널별 메시지 목록 조회
    void update(UUID id, String text);
    void delete(UUID id);

}
