package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
  List<Message> findAllByChannel_Id(UUID channelId);

  Slice<Message> findAllByChannel_Id(UUID channelId, Pageable pageable);

  void deleteAllByChannel_Id(UUID channelId);

  Optional<Message> findTopByChannel_IdOrderByCreatedAtDesc(UUID channelId);




  //Message save(Message message);

  //Optional<Message> findById(UUID id);

  //boolean existsById(UUID id);

  //void deleteById(UUID id);

}
