package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message createMessage(User user, Channel channel, String content);
    Message findMessage(UUID messageId);
    List<Message> findAllMessage();
    List<Message> findAllByChannelMessage(UUID channelId);
    List<Message> findAllByUserMessage(UUID userId);
    void deleteMessage(UUID messageId);
    Message updateMessage(UUID messageId, String content);
}
