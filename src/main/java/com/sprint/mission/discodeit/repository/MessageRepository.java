package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);

    void deleteAllByChannelId(UUID channelId);

    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.author a LEFT JOIN FETCH a.profile p JOIN FETCH a.status "
    + "WHERE m.channel.id = :channelId AND m.createdAt < :createdAt")
    Slice<Message> findAllByChannelIdWithAuthor(UUID channelId, Instant createdAt, Pageable pageable);
}
