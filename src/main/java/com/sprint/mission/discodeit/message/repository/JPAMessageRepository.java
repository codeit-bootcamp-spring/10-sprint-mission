package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.channel.entity.Channel;
import com.sprint.mission.discodeit.message.entity.Message;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JPAMessageRepository extends JpaRepository<Message, UUID> {


  Optional<Message> findFirstByChannelOrderByCreatedAtDesc(Channel channel);

  @Query("""
      SELECT m.channel.id, MAX(m.createdAt)
      FROM Message m
      WHERE m.channel.id IN :channelIds
      GROUP BY m.channel.id
      """)
  List<Object[]> findLastMessageTimesByChannelIds(@Param("channelIds") List<UUID> channelIds);

  Slice<Message> findByChannelId(UUID channelId, Pageable pageable);

  @Query("""
      SELECT m FROM Message m
      WHERE m.channel.id = :channelId
      AND (CAST(:cursor AS java.time.Instant) IS NULL OR m.createdAt < :cursor)
      ORDER BY m.createdAt DESC
      """)
  Slice<Message> findByChannelIdWithCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable
  );

}
