package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.repository.DomainRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class JCFDomainRepository<T> implements DomainRepository<T> {
    private final Map<UUID, T> data;

    public JCFDomainRepository(Map<UUID, T> data) {
        this.data = data;
    }

    protected Map<UUID, T> getData() {
        return data;
    }

    @Override
    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public <R> R streamAll(Function<Stream<T>, R> action) {
        return action.apply(data.values().stream());
    }

    @Override
    public boolean anyMatch(Predicate<T> predicate) {
        return streamAll(stream -> stream.anyMatch(predicate));
    }

    @Override
    public Stream<T> filter(Predicate<T> predicate) {
        return streamAll(stream -> stream.filter(predicate));
    }
}
