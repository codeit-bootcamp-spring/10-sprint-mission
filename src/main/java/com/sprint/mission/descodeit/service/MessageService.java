package com.sprint.mission.descodeit.service;
import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import java.util.Set;
import java.util.UUID;

public interface MessageService {
    void create(Message message);
    Message findMessages(UUID MessageID);
    Set<UUID> findAllMessages();
    void update(UUID messageID, String newText);
    void delete(UUID messageID);
}
