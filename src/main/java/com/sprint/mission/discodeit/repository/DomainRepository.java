package com.sprint.mission.discodeit.repository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface DomainRepository<T> {
    T save(T entity) throws IOException;

    Optional<T> findById(UUID id) throws IOException, ClassNotFoundException;

    boolean existsById(UUID id);

    void deleteById(UUID id) throws IOException;
}
