package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "channel", "attachments"})
    @Query("""
        select m
        from Message m
        where m.author.id = :authorId
            and m.createdAt < :cursor
        order by m.createdAt desc
    """)        // m.createdAt < :cursor = cursor 시간보다 더 이전에 작성된 메시지를 최신순으로 조회
    Slice<Message> findByAuthorIdWithCursor(UUID authorId, Instant cursor, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "channel", "attachments"})
    Slice<Message> findByAuthor_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "channel", "attachments"})
    @Query("""
        select m
        from Message m
        where m.channel.id = :channelId
            and m.createdAt < :cursor
        order by m.createdAt desc
    """)        // m.createdAt < :cursor = cursor 시간보다 더 이전에 작성된 메시지를 최신순으로 조회
    Slice<Message> findByChannelIdWithCursor(UUID channelId, Instant cursor, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "channel", "attachments"})
    Slice<Message> findByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "author.userStatus", "author.profile", "channel", "attachments"})
    Optional<Message> findById(UUID messageId);

    @EntityGraph(attributePaths = {"author", "channel", "attachments"})
    List<Message> findAllByAuthor_Id(UUID userId);

    // 하나의 채널에서 전송된 메시지 조회라 channel은 attributePaths에서 제외함
    @EntityGraph(attributePaths = {"author", "attachments"})
    List<Message> findAllByChannel_Id(UUID channelId);

    Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);

    @Query("""
        select m.channel.id, max(m.createdAt)
        from Message m
        where m.channel.id in :channelIds
        group by m.channel.id
    """)    // 채널의 마지막 메시지를 찾음
    List<Object[]> findLastMessageAtByChannelIds(List<UUID> channelIds);

    void deleteAllByChannel_Id(UUID channelId);
}
