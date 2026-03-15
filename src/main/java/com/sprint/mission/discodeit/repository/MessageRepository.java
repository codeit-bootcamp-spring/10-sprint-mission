package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findAllByChannelId(UUID channelId);
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);
    void deleteAllByChannelId(UUID channelId);
    Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId);
}
