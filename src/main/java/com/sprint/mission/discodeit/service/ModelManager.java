package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModelManager<T extends Entity<T>> {
    void create(T entity);

    Optional<T> read(UUID uuid);

    List<T> readAll(List<UUID> uuids);

    List<T> readAll();

    void update(UUID uuid, String value);

    void delete(UUID key);
}
