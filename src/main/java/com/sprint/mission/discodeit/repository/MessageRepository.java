package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"author", "author.profile", "author.status", "attachments"})
    Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.profile", "author.status", "attachments"})
    Slice<Message> findByChannelIdAndCreatedAtLessThan(UUID channelId, Instant cursor, Pageable pageable);

    void deleteAllByChannelId(UUID channelId);

    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);
}
