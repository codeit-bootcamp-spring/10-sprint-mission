package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findByChannel_Id(UUID channelId);

    Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);

    void deleteAllByChannel_Id(UUID channelId);
}
