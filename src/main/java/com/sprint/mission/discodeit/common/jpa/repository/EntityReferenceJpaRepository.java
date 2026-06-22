package com.sprint.mission.discodeit.common.jpa.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EntityReferenceJpaRepository<T> extends JpaRepository<T, UUID> {

  @Query("select t.id from #{#entityName} t where t.id in :ids")
  List<UUID> filterExistingIds(Collection<UUID> ids);
}
