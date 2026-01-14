package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Entity;

import java.util.List;
import java.util.UUID;

public interface RelationManager<T extends Entity<T>, U extends Entity<U>> {
    void create(T entity1, U entity2);

    List<UUID> read(T entity);

    void update(T entity, U oldValue, U newValue);

    void delete(UUID uuid1, UUID uuid2);

    void deleteKey(UUID key);
}
