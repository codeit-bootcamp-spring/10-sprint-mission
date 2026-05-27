package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.message.ChannelLastMessageAtDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query(value = "SELECT DISTINCT m FROM Message AS m " +
            "LEFT JOIN FETCH m.channel " +
            "LEFT JOIN FETCH m.author " +
            "LEFT JOIN FETCH m.attachments " +
            "WHERE m.id = :messageId")
    Optional<Message> findByIdWithAuthorAndChannel(@Param("messageId") UUID messageId);

    @Query(value = "SELECT max(m.createdAt) AS lastMessageAt " +
            "FROM Message AS m " +
            "WHERE m.channel.id = :channelId")
    Optional<Instant> findLastMessageAtByChannelId(@Param("channelId") UUID channelId);

    @Query(value = "SELECT new com.sprint.mission.discodeit.dto.message.ChannelLastMessageAtDto(m.channel.id, max(m.createdAt)) " +
            "FROM Message AS m " +
            "WHERE m.channel.id IN :channelIds " +
            "GROUP BY m.channel.id")
    List<ChannelLastMessageAtDto> findLastMessageAtDtoByChannelIds(@Param("channelIds") List<UUID> channelIds);

    @Query(value = "SELECT DISTINCT m FROM Message AS m " +
            "LEFT JOIN FETCH m.channel AS c " +
            "LEFT JOIN FETCH m.author AS a " +
            "LEFT JOIN FETCH a.profile " +
            "WHERE c.id = :channelId AND m.createdAt < :createdAt " +
            "ORDER BY m.createdAt DESC, m.id DESC")
    Slice<Message> findAllByChannelId(@Param("channelId") UUID channelId, @Param("createdAt") Instant createdAt, Pageable pageable);
}
