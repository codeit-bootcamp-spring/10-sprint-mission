package com.sprint.mission.discodeit.message.infra.repository;

import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.domain.repository.MessageRepository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageJpaRepository extends MessageRepository, JpaRepository<Message, UUID> {

  @Override
  @EntityGraph(attributePaths = {"author", "author.profile"})
  Slice<Message> findSliceByChannel_Id(UUID channelId, Pageable pageable);

  @Override
  @Query("SELECT DISTINCT m FROM Message m JOIN m.attachments a WHERE a.id IN :attachmentIds")
  List<Message> findMessagesByAttachmentIds(Collection<UUID> attachmentIds);
}
