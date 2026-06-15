package com.sprint.mission.discodeit.binarycontent.infra.repository;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BinaryContentRepository extends DomainRepository<BinaryContent> {

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update BinaryContent b set b.status = :status where b.id in :ids")
  int updateStatus(Collection<UUID> ids, BinaryContentStatus status);

  @Query("select count(b) > 0 from BinaryContent b where b.id = :id and b.status = 'SUCCESS'")
  boolean existsSuccessById(UUID id);
}
