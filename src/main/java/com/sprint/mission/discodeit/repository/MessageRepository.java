package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
//    Message save(Message message);
//    Optional<Message> findById(UUID id);

//    boolean existsById(UUID id);
//    void delete(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    void deleteAllByChannelId(UUID channelId);
}
