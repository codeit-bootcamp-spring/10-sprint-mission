package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Message findTopByChannelIdOrderByUpdatedAtDesc(UUID channelId);

    Message findTopByChannelIdOrderByCreatedAtDesc(UUID channelId);

    List<Message> findByChannelId(UUID channelId);

}


