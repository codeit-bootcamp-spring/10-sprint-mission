package com.sprint.mission.discodeit.message.infra.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;

public interface MessageRepository extends DomainRepository<Message> {

  @EntityGraph(attributePaths = {"author", "author.profile"})
  Slice<Message> findSliceByChannel_Id(UUID channelId, Pageable pageable);

  @EntityGraph(attributePaths = {"author", "channel", "attachments", "author.profile"})
  Optional<Message> findWithFetchJoinById(UUID uuid);
}
