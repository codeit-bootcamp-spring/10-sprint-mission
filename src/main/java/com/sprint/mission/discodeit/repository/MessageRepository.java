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

    @Query("SELECT m FROM Message m JOIN FETCH m.author JOIN FETCH m.channel LEFT JOIN FETCH m.attachments WHERE m.author.id = :userId")
    Slice<Message> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    void deleteByChannelId(UUID channelId);

    void deleteByAuthorId(UUID authorId);

    Optional<Message> findFirstByChannelIdOrderByCreatedAtDescIdDesc(UUID channelId);

    @Query("SELECT m FROM Message m " +
            "JOIN FETCH m.author " +
            "JOIN FETCH m.channel " +
            "WHERE m.channel.id = :channelId " +
            "ORDER BY m.createdAt DESC, m.id DESC")
    Slice<Message> findAllByChannelId(
            @Param("channelId") UUID channelId,
            Pageable pageable);

    @Query("SELECT m FROM Message m "
            + "LEFT JOIN FETCH m.author a "
            + "LEFT JOIN FETCH a.profile "
            + "WHERE m.channel.id = :channelId AND m.createdAt < :cursor "
            + "ORDER BY m.createdAt DESC, m.id DESC")
    Slice<Message> findAllByChannelIdBeforeCursor(
            @Param("channelId") UUID channelId,
            @Param("cursor") Instant cursor,
            Pageable pageable);

    @Query("SELECT m FROM Message m "
            + "WHERE m.channel.id IN :channelIds "
            + "AND m.createdAt = ("
            + "  SELECT MAX(m2.createdAt) "
            + "  FROM Message m2 "
            + "  WHERE m2.channel.id = m.channel.id"
            + ")")
    List<Message> findLastMessagesByChannelIds(@Param("channelIds") List<UUID> channelIds);
}
