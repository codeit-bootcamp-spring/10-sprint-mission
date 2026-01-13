package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String contents, UUID userID, UUID channelID);
    Message find(UUID messageID);
    List<Message> findAll();
    Message update(UUID messageID, String contents);
    void delete(UUID messageID);
}
