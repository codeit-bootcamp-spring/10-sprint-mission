package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends DomainRepository<Message> {
    List<Message> findAllByChannelId(UUID channelId);
}
