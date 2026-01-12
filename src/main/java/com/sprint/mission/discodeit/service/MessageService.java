package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(User user, Channel channel, String content);

    List<Message> getAllMessages();

    List<Message> getMessageListByUser(UUID userId);

    List<Message> getMessageListByChannel(UUID channelId);

    Optional<Message> getMessageByMessageId(UUID messageId);

    void updateMessage(UUID messageId, String newContent);

    void deleteMessage(UUID messageId);

    void clearChannelMessage(UUID channelId);
}
