package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Modifying
    @Query("DELETE FROM Message m WHERE m.channel.id = :channelId")
    void deleteAllByChannelId(@Param("channelId") UUID channelId);

    List<Message> findAllByAuthor_Id(UUID authorId);

    List<Message> findAllByChannelIdIn(List<UUID> channelIds);


    @Query("SELECT m FROM Message m " +
            "LEFT JOIN FETCH m.author a " +
            "LEFT JOIN FETCH a.profile " +
            "WHERE m.channel.id=:channelId AND m.createdAt < :createdAt")
    Slice<Message> findAllByChannelIdWithAuthor(@Param("channelId") UUID channelId,
                                                @Param("createdAt") Instant createdAt,
                                                Pageable pageable);

    @Query("SELECT Max(m.createdAt) FROM Message m WHERE m.channel.id = :channelId")
    Instant findFirstByChannelIdOrderByCreatedAtDesc(@Param("channelId") UUID channelId);

    @Query("SELECT m.author.id FROM Message m WHERE m.id = :messageId")
    Optional<UUID> findAuthorIdById(@Param("messageId") UUID messageId);
}
