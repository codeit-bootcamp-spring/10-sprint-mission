package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(UUID channelId, UUID senderId, String content);

    Message findMessage(UUID channelId, UUID messageId);

    List<Message> findAllMessage(UUID channelId);

    List<Message> findAllByChannelMessage(Channel channel);

    Message updateMessage(UUID channelId, UUID messageId, String newContent);

    void deleteMessage(UUID channelId, UUID messageId);
}
