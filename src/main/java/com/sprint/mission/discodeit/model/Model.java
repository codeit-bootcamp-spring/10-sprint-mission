package com.sprint.mission.discodeit.model;

import com.sprint.mission.discodeit.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Model<T extends Entity<T>> {
    private final Map<UUID, T> data;

    public Model() {
        this(new HashMap<>());
    }

    public Model(Map<UUID, T> data) {
        this.data = data;
    }

    public boolean contain(UUID key) {
        return data.containsKey(key);
    }

    public T get(UUID key) {
        return data.get(key).copy();
    }

    public List<T> getAll(List<UUID> keys) {
        return keys.stream()
                .map(key -> data.computeIfPresent(key, (uuid, entity) -> entity))
                .toList();
    }

    public void remove(UUID key) {
        data.remove(key);
    }

    public void add(UUID key, T value) {
        data.put(key, value);
    }

    public void update(UUID key, String value) {
        T entity = data.get(key);
        entity.update(value);
    }
}
