package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);
    Slice<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId, Instant cursor, Pageable pageable);
    Slice<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);
    List<Message> findAllByChannel(Channel channel);
}
