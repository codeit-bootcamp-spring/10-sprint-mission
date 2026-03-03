package com.sprint.mission.discodeit.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface DomainRepository<T> {
    T save(T entity);

    Optional<T> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    <R> R streamAll(Function<Stream<T>, R> action);

    boolean anyMatch(Predicate<T> predicate);

    Stream<T> filter(Predicate<T> predicate);
}
