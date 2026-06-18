package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("SELECT m FROM Message m " +
      "LEFT JOIN FETCH m.author a " +
      "WHERE m.channel.id = :channelId ")
  List<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

  @Query("SELECT m FROM Message m " +
      "LEFT JOIN FETCH m.author a " +
      "WHERE m.channel.id = :channelId AND m.createdAt < :cursor ")
  List<Message> findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(UUID channelId,
      Instant cursor, Pageable pageable);

  Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);

  void deleteAllByChannelId(UUID channelId);
}
