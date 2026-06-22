package com.sprint.mission.discodeit.binarycontent.domain.repository;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository extends DomainRepository<BinaryContent> {

  int updateStatus(Collection<UUID> ids, BinaryContentStatus status);

  boolean existsSuccessById(UUID id);

  List<BinaryContent> findAllByIdIn(Collection<UUID> ids);
}
