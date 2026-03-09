package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Slice<Message> findByAuthor_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Slice<Message> findByChannel_IdOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);

    void deleteAllByChannel_Id(UUID channelId);
}
