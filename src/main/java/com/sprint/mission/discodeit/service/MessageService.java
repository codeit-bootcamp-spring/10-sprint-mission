package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public interface MessageService {

    Message create(String msg, UUID userId, UUID channelId);

    Message read(UUID id);

    List<Message> readAll();

    Message update(UUID id, String messageData);

    void delete(UUID id);

    List<Message> readUserMessageList(UUID userId);

    List<Message> readChannelMessageList(UUID channelId);
}
