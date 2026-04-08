package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @EntityGraph(attributePaths = {"author", "attachments", "author.status", "author.profile"})
  @Query("select m from Message m join fetch m.channel c where c.id = :channelId")
  List<Message> findAllByChannelId(@Param("channelId") UUID channelId);

  @EntityGraph(attributePaths = {"author", "channel", "attachments", "author.status",
      "author.profile"})
  Optional<Message> findWithFetchJoinById(UUID uuid);
}
