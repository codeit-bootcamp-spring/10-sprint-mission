package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainRepository<T> {
    T save(T entity);
    Optional<T> findById(UUID id);
    List<T> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
