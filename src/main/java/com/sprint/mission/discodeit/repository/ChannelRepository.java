package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> findById(UUID id);
    List<Channel> findAllByUserId(UUID userId);
    List<Channel> findAll();
    void delete(UUID id);
    void deleteMessageByMessageId(UUID channelId, UUID messageId);
    void deleteByUserId(UUID userId);
    void clear();
}
