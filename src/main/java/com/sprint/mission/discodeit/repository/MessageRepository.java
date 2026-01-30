package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    Message saveMessage(Message message);
    Optional<Message> findMessageByMessageId(UUID messageId);
    List<Message> findAll();
    void deleteMessage(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);
    void deleteAllByChannelId(UUID channelId);
    Optional<Instant> findLatestMessageTimeByChannelId(UUID channelId);
}
