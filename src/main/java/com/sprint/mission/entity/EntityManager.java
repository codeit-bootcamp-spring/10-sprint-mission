package com.sprint.mission.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityManager<T extends Entity<T>> implements Serializable {
    private static final long serialVersionUID = 20260120L;
    private final Map<UUID, T> data = new HashMap<>();

    public void add(T entity) {
        data.put(entity.getId(), entity);
    }

    public T get(UUID id) {
        return data.get(id).copy();
    }

    public void remove(UUID id) {
        data.remove(id);
    }

    public Map<UUID, T> getEntitiesById(UUID id, UUID... ids) {
        return Stream.concat(Stream.of(id), Arrays.stream(ids))
                .collect(Collectors.toMap(uuid -> uuid, this::get));
    }

    public boolean hasEntity(UUID id) {
        return data.containsKey(id);
    }
}
