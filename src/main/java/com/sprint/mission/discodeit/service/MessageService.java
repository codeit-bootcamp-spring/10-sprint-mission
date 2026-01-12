package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MessageService {
    Message create(String contents, UUID senderID, UUID channelID);
    Message read(UUID messageID);
    List<Message> readAll();
    void update(UUID messageID, String contents);
    void delete(UUID messageID);
}
