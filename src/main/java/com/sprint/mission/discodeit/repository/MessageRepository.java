package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("""
      SELECT m.id
      FROM Message m
      WHERE m.channel.id = :channelId
      """)
  Slice<UUID> findIdsByChannelId(
      @Param("channelId") UUID channelId,
      Pageable pageable
  );

  @Query("""
      SELECT m.id
      FROM Message m
      WHERE m.channel.id = :channelId
        AND m.createdAt < :cursor
      """)
  Slice<UUID> findIdsByChannelIdBeforeCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable
  );

  @Query("""
      SELECT DISTINCT m
      FROM Message m
      JOIN FETCH m.channel
      LEFT JOIN FETCH m.author a
      LEFT JOIN FETCH a.profile
      LEFT JOIN FETCH a.userStatus
      LEFT JOIN FETCH m.attachments
      WHERE m.id IN :messageIds
      """)
  List<Message> findAllWithDetailsByIdIn(@Param("messageIds") List<UUID> messageIds);

  @Query("""
      SELECT DISTINCT m
      FROM Message m
      JOIN FETCH m.channel
      LEFT JOIN FETCH m.author a
      LEFT JOIN FETCH a.profile
      LEFT JOIN FETCH a.userStatus
      LEFT JOIN FETCH m.attachments
      WHERE m.id = :messageId
      """)
  Optional<Message> findWithDetailsById(@Param("messageId") UUID messageId);

  @Query("""
      SELECT m.channel.id as channelId,
             max(m.createdAt) as lastMessageAt
      FROM Message m
      WHERE m.channel.id in :channelIds
      GROUP BY m.channel.id
      """)
  List<ChannelLastMessageAtProjection> findLastMessageAtByChannelIdIn(
      @Param("channelIds") List<UUID> channelIds
  );

  @Query("""
      SELECT DISTINCT m
      FROM Message m
      LEFT JOIN FETCH m.attachments
      WHERE m.channel.id = :channelId
      """)
  List<Message> findAllByChannelIdWithAttachments(@Param("channelId") UUID channelId);

  void deleteByChannelId(UUID channelId);

  interface ChannelLastMessageAtProjection {

    UUID getChannelId();

    Instant getLastMessageAt();
  }
}