package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.repository.DomainRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    public List<T> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
