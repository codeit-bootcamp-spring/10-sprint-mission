package com.sprint.mission.service;

import com.sprint.mission.entity.Entity;
import com.sprint.mission.service.validation.Validation;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface BaseService<T extends Entity> extends Validation {
    T create(String value);
    T get(UUID uuid);
    List<T> getAll(List<UUID> uuids);
    void update(UUID uuid, String newValue);
    void delete(UUID uuid);

    default T create(String value, Map<UUID, T> data, Function<String, T> factory) {
        validateNotNull(value);
        T entity = factory.apply(value);
        data.put(entity.getUuid(), entity);
        return entity;
    }

    default T get(UUID uuid, Map<UUID, T> data) {
        validateNotNull(uuid);
        validateHavingUUID(uuid, data);
        return data.get(uuid);
    }

    default List<T> getAll(List<UUID> uuids, Map<UUID, T> data) {
        validateNotNull(uuids);
        return uuids.stream().map(uuid -> get(uuid, data)).toList();
    }

    default void update(UUID uuid, String newValue, Map<UUID, T> data) {
        validateNotNull(newValue);
        T entity = get(uuid, data);
        entity.update(newValue);
    }

    default void delete(UUID uuid, Map<UUID, T> data) {
        validateNotNull(uuid);
        validateHavingUUID(uuid, data);
        data.remove(uuid);
    }
}
