package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  interface ChannelLastMessageAtProjection {
    UUID getChannelId();
    Instant getLastMessageAt();
  }

  @EntityGraph(attributePaths = {
          "author",
          "author.profile",
          "author.status"
  })
  @Query("""
      select m
      from Message m
      where m.channel.id = :channelId
        and (
          :cursorCreatedAt is null
          or m.createdAt < :cursorCreatedAt
          or (m.createdAt = :cursorCreatedAt and m.id < :cursorId)
        )
      order by m.createdAt desc, m.id desc
      """)
  List<Message> findCursorPageByChannelId(
          @Param("channelId") UUID channelId,
          @Param("cursorCreatedAt") Instant cursorCreatedAt,
          @Param("cursorId") UUID cursorId,
          Pageable pageable
  );

  @EntityGraph(attributePaths = {
          "author",
          "author.profile",
          "author.status",
          "attachments"
  })
  @Query("""
      select m
      from Message m
      where m.id = :messageId
      """)
  Optional<Message> findWithDetailsById(@Param("messageId") UUID messageId);

  Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);

  @Query("""
      select m.channel.id as channelId, max(m.createdAt) as lastMessageAt
      from Message m
      where m.channel.id in :channelIds
      group by m.channel.id
      """)
  List<ChannelLastMessageAtProjection> findLastMessageAtByChannelIds(
          @Param("channelIds") Collection<UUID> channelIds
  );

  void deleteAllByChannel_Id(UUID channelId);
}