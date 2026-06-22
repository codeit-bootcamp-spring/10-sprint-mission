package com.sprint.mission.discodeit.message.domain.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.message.domain.entity.Message;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageRepository extends DomainRepository<Message> {

  Slice<Message> findSliceByChannel_Id(UUID channelId, Pageable pageable);

  List<Message> findMessagesByAttachmentIds(Collection<UUID> attachmentIds);
}
