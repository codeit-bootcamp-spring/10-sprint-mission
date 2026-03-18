package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, UUID> {

  Slice<Message> findByChannel_Id(UUID channelId, Pageable pageable);

  List<Message> findAllByChannel_Id(UUID channelId);

  default List<Message> findAllByChannelId(UUID channelId) {
    return findAllByChannel_Id(channelId);
  }

  default void delete(UUID id) {
    deleteById(id);
  }
}