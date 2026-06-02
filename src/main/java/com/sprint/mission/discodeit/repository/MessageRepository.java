package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"author", "author.status", "author.profile", "channel"})
    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.status", "author.profile", "channel"})
    Optional<Message> findById(@NonNull UUID messageId);

    @EntityGraph(attributePaths = {"author", "author.status", "author.profile", "channel"})
    @Query("""
    select m
    from Message m
    where m.channel.id = :channelId
    order by m.createdAt desc
    """)
    Slice<Message> findLatestByChannelId(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.status", "author.profile", "channel"})
    @Query("""
    select m
    from Message m
    where m.channel.id = :channelId
        and m.createdAt < :cursor
    order by m.createdAt desc
    """)
    Slice<Message> findAllUseCursorByChannelId(@Param("channelId")UUID channelId, @Param("cursor") Instant Cursor, Pageable pageable);

    @Query("""
    select m.id, a
    from Message m
    left join m.attachments a
    where m.id in :messageIds
    """)
    List<Object[]> findAttachmentsByMessageIds(@Param("messageIds") List<UUID> messageIds);

    Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);

    void deleteByChannelId(UUID channelId);

  boolean existsByIdAndAuthor_Id(UUID messageId, UUID userId);
}
