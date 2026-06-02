package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
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

  @Query("SELECT m FROM Message m "
      + "LEFT JOIN FETCH m.author a "
      + "LEFT JOIN FETCH a.profile "
      + "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
  Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
      @Param("createdAt") Instant createdAt,
      Pageable pageable);


  @Query("SELECT m.createdAt "
      + "FROM Message m "
      + "WHERE m.channel.id = :channelId "
      + "ORDER BY m.createdAt DESC LIMIT 1")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

  void deleteAllByChannelId(UUID channelId);

  @Query("SELECT new com.sprint.mission.discodeit.dto.data.NotificationDto("
      + "m.id, m.createdAt, r.user.id, COALESCE(c.name, a.username, 'New message'), m.content) "
      + "FROM ReadStatus r "
      + "JOIN r.channel c "
      + "JOIN Message m ON m.channel = c "
      + "LEFT JOIN m.author a "
      + "WHERE r.user.id = :userId "
      + "AND r.notificationEnabled = true "
      + "AND m.createdAt > r.lastReadAt "
      + "AND (a IS NULL OR a.id <> :userId) "
      + "ORDER BY m.createdAt DESC")
  List<NotificationDto> findUnreadNotificationsByUserId(@Param("userId") UUID userId);

  /// fetch join으로 channel과 author(user)의 정보를 한꺼번에 가져온다.
  @Query("""
    SELECT m
    FROM Message m
    JOIN FETCH m.channel
    LEFT JOIN FETCH m.author
    WHERE m.id = :messageId
  """)
  Optional<Message> findByIdWithAuthorAndChannel(@Param("messageId") UUID messageId);
}
