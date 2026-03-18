package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Slice<Message> findByChannel_IdOrderByCreatedAtDescIdDesc(UUID channelId, Pageable pageable);

    @Query("""
            select m
            from Message m
            where m.channel.id = :channelId
              and (
                    m.createdAt < :cursorCreatedAt
                    or (m.createdAt = :cursorCreatedAt and m.id < :cursorId)
              )
            order by m.createdAt desc, m.id desc
            """)
    Slice<Message> findNextPageByChannelIdAndCursor(
            UUID channelId,
            Instant cursorCreatedAt,
            UUID cursorId,
            Pageable pageable
    );
}
