package com.sprint.mission.discodeit.repository.querydsl;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelQDSLRepository {
    Optional<Channel> findByIdWithLastMessageAt(UUID id);

    List<Channel> findAllWithLastMessageAt();

    List<Channel> findVisibleToWithLastMessageAt(UUID userId);
}
