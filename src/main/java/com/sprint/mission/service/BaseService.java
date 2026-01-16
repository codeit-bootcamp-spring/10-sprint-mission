package com.sprint.mission.service;

import com.sprint.mission.entity.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface BaseService<T extends Entity> {
    T get(UUID id);
    <U extends Collection<UUID>> Map<UUID, T> getAll(U ids);
    void update(UUID id, String newValue);
    void delete(UUID id);
    boolean hasId(UUID id);
}
