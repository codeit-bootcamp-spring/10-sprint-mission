package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update BinaryContent b set b.status = :status where b.id in :ids")
  int updateStatus(Collection<UUID> ids, BinaryContentStatus status);

  @Query("select bc from BinaryContent bc where bc.id = :id and bc.status = 'COMPLETED'")
  Optional<BinaryContent> findCompletedById(UUID id);

  @Query("select bc from BinaryContent bc where bc.id in :ids and bc.status = 'COMPLETED'")
  List<BinaryContent> findAllCompletedByIds(Collection<UUID> ids);
}
