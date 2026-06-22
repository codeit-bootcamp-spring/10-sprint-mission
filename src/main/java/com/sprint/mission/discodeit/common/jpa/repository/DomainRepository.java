package com.sprint.mission.discodeit.common.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainRepository<T> {

  T save(T entity);

  <S extends T> List<S> saveAll(Iterable<S> entities);

  void delete(T entity);

  Optional<T> findById(UUID id);
}
