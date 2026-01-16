package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Entity;
import com.sprint.mission.service.BaseService;

import java.util.*;
import java.util.stream.Collectors;

public abstract class JCFBaseService<T extends Entity> implements BaseService<T> {
    private final Map<UUID, T> data;
    private final String idNotExistMessage = "don't exist, %s";

    public JCFBaseService() {
        this.data = new HashMap<>();
    }

    public JCFBaseService(Map<UUID, T> data) {
        this.data = data;
    }

    protected Map<UUID, T> getData() {
        return data;
    }

    @Override
    public T get(UUID id) {
        if (hasId(id)) {
            return data.get(id);
        }
        throw new IllegalArgumentException(String.format(idNotExistMessage, id));
    }

    @Override
    public <U extends Collection<UUID>> Map<UUID, T> getAll(U ids) {
        U notNullIds = Objects.requireNonNull(ids);
        return notNullIds.stream()
                .map(this::get)
                .collect(Collectors.toMap(T::getId, entity -> entity));
    }

    @Override
    public void update(UUID id, String newValue) {
        T entity = get(id);
        entity.update(newValue);
    }

    @Override
    public void delete(UUID id) {
        if (hasId(id)) {
            data.remove(id);
            return;
        }
        throw new IllegalArgumentException(String.format(idNotExistMessage, id));
    }

    @Override
    public boolean hasId(UUID id) {
        UUID notNullId = Objects.requireNonNull(id);
        return data.containsKey(notNullId);
    }
}
