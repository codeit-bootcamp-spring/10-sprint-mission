package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Optional<Message> findFirstByChannelOrderByCreatedAtDesc(Channel channel);

  // [추가]: 모든 채널의 최신 메시지 시각을 한 번의 쿼리로 가져오기 (N+1 방지용)
  @Query("SELECT m.channel.id, MAX(m.createdAt) FROM Message m GROUP BY m.channel.id")
  List<Object[]> findAllLastMessageAt();

  // 1. 커서가 없을 때 (첫 페이지 조회)
  @Query("SELECT m FROM Message m JOIN FETCH m.author " +
      "WHERE m.channel = :channel ORDER BY m.createdAt DESC")
  List<Message> findByChannelOrderByCreatedAtDesc(Channel channel, Pageable pageable);

  // 2. 커서가 있을 때 (다음 페이지 조회)
  @Query("SELECT m FROM Message m JOIN FETCH m.author " +
      "WHERE m.channel = :channel AND m.createdAt < :cursor " +
      "ORDER BY m.createdAt DESC")
  List<Message> findByChannelAndCursor(@Param("channel") Channel channel,
      @Param("cursor") Instant cursor,
      Pageable pageable);

  @EntityGraph(attributePaths = {"author", "attachments"})
  Optional<Message> findWithAuthorAndAttachmentsById(UUID id);

  @Query("""
      SELECT m
      FROM Message m
      JOIN FETCH m.author
      JOIN FETCH m.channel
      WHERE m.id = :messageId
      """)
  Optional<Message> findWithAuthorAndChannelById(UUID messageId);

  long countByChannel(Channel channel);

  void deleteByChannel(Channel channel);
}
