package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  @Query("""
      select m.id
      from Message m
      where m.channel.id = :channelId
      order by m.createdAt desc
      """)
  List<UUID> findMessageIdsByChannelId(
      @Param("channelId") UUID channelId,
      Pageable pageable
  );

  @Query("""
      select m.id
      from Message m
      where m.channel.id = :channelId
        and m.createdAt < :cursor
      order by m.createdAt desc
      """)
  List<UUID> findMessageIdsByChannelIdAndCursor(
      @Param("channelId") UUID channelId,
      @Param("cursor") Instant cursor,
      Pageable pageable
  );

  @Query("""
      select distinct m
      from Message m
      join fetch m.author
      left join fetch m.attachments
      where m.id in :messageIds
      """)
  List<Message> findAllByIdInWithAuthorAndAttachments(
      @Param("messageIds") List<UUID> messageIds
  );

  List<Message> findAllByChannel_Id(UUID channelId);

  long countByChannel_Id(UUID channelId);

  default List<Message> findAllByChannelId(UUID channelId) {
    return findAllByChannel_Id(channelId);
  }

  default void delete(UUID id) {
    deleteById(id);
  }
}