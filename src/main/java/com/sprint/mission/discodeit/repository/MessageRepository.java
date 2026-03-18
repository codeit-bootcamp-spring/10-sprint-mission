package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
//    Message save(Message message);
//    Optional<Message> findById(UUID id);

//    boolean existsById(UUID id);
//    void delete(UUID id);
    Page<Message> findAllByChannelId(UUID channelId, Pageable pageable);
    void deleteAllByChannelId(UUID channelId);

    @Query("""
    SELECT m.channel.id, MAX(m.createdAt)
    FROM Message m
    WHERE m.channel.id IN :channelIds
    GROUP BY m.channel.id
    """)//JPQL은 테이블이 아닌 Entity 기준으로 작성된다.
        //채널별 마지막 메시지 시간만 조회(ChannelService내 toDto에 사용), lastMessageAt을 위한 메서드
    List<Object[]> findLastMessageAtByChannelIds(List<UUID> channelIds);
    Optional<Message> findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

}
