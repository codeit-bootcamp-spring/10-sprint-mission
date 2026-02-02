package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    void save(Message message);

    Optional<Message> findById(UUID id);

    List<Message> findAll();

    void delete(Message message);

    void deleteByUser(User user);

    void deleteByChannel(Channel channel);

    Instant findLastMessageAtByChannelId(UUID id);

    Map<UUID, Instant> findLastMessageAtByChannelIds(List<UUID> channelIds);

    void deleteById(UUID messageId);
}
