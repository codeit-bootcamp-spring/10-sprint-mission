package com.sprint.mission.discodeit.model;

import com.sprint.mission.discodeit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Model<T extends Entity<T>> {
    private final List<T> data;

    public Model() {
        this(new ArrayList<>());
    }

    public Model(List<T> data) {
        this.data = data;
    }

    public boolean containsKey(UUID key) {
        return data.stream().anyMatch(entity -> key.equals(entity.getUuid()));
    }

    private T get(UUID key, boolean isCopy) {
        return data.stream()
                .filter(entity -> key.equals(entity.getUuid()))
                .findFirst()
                .map(entity -> isCopy ? entity.copy() : entity)
                .orElse(null);
    }

    public T get(UUID key) {
        return get(key, true);
    }

    public List<T> getAll(List<UUID> keys) {
        return keys.stream()
                .map(this::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public List<T> getAll() {
        return data.stream().map(T::copy).toList();
    }

    public void remove(UUID key) {
        data.removeIf(entity -> key.equals(entity.getUuid()));
    }

    public void add(T value) {
        if (!containsKey(value.getUuid())) {
            data.add(value);
        }
    }

    public void update(UUID key, String value) {
        T entity = get(key, false);
        if (Objects.nonNull(entity)) {
            entity.update(value);
        }
    }

    public void remove(List<UUID> uuids) {
        uuids.forEach(this::remove);
    }
}
