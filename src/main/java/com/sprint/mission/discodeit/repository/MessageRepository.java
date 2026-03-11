package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "attachments", "author.profile", "author.status"})
  List<Message> findByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

  @EntityGraph(attributePaths = {"author", "attachments", "author.profile", "author.status"})
  List<Message> findByChannel_IdAndCreatedAtBeforeOrderByCreatedAtDesc(
          UUID channelId,
          Instant cursor,
          Pageable pageable
  );

  void deleteAllByChannel_Id(UUID channelId);

  Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);

}
