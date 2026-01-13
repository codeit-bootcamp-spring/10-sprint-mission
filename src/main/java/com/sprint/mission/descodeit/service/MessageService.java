package com.sprint.mission.descodeit.service;
import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;

import java.util.*;
import java.util.UUID;

public interface MessageService {
    void setDependencies(UserService userService, ChannelService channelService);
    Message create(UUID userId, String text, UUID channelId);
    Message findMessage(UUID messageId);
    List<Message> findAllMessages();
    Message update(UUID messageId, String newText);
    void delete(UUID messageId);
}
