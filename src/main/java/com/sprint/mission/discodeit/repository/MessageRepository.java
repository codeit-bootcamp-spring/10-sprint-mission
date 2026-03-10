package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findAllByChannelId(UUID channelId);

  void deleteByChannelId(UUID channelId);

  @Query("SELECT MAX(m.createdAt) FROM Message m WHERE m.channel = :channel")
  Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);
}
