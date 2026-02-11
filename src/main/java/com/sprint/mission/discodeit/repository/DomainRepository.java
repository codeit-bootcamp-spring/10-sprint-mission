package com.sprint.mission.discodeit.repository;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface DomainRepository<T> {
    T save(T entity) throws IOException;

    Optional<T> findById(UUID id) throws IOException, ClassNotFoundException;

    boolean existsById(UUID id);

    void deleteById(UUID id) throws IOException;

    <R> R streamAll(Function<Stream<T>, R> action) throws IOException;

    boolean anyMatch(Predicate<T> predicate) throws IOException;

    Stream<T> filter(Predicate<T> predicate) throws IOException;
}
