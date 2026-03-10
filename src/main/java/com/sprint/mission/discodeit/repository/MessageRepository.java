package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {


    Message findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);


    @Query("""
          SELECT m
          FROM Message m 
          join fetch m.channel c
          where c.id in :channelIds
          AND m.createdAt = (
                  SELECT MAX(m2.createdAt)
                  FROM Message m2
                  WHERE m2.channel.id = c.id
          )
        """)
    List<Message> findLastMessageByChannelIds(List<UUID> channelIds);

    @EntityGraph(attributePaths = {"channel"})
    List<Message> findByChannelId(UUID channelId);

    @EntityGraph(attributePaths = {
        "author", "author.userStatus", "author.profile"
    })
    Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {
        "author", "author.userStatus", "author.profile"
    })
    @Query("""
        SELECT m
        FROM Message m
        WHERE m.channel.id = :channelId
          AND (:cursor IS NULL OR m.createdAt < :cursor)
        """)
    Slice<Message> findByChannelIdAndCursor(UUID channelId, Optional<Instant> cursor,
        Pageable pageable);

}


