package com.sprint.mission.service.basic;

import com.sprint.mission.entity.Entity;
import com.sprint.mission.entity.EntityType;
import com.sprint.mission.repository.BaseRepository;
import com.sprint.mission.service.BaseService;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public abstract class BasicBaseService<T extends Entity<T>, U extends BaseRepository<T>> implements BaseService<T> {
    private final String ID_NOT_FOUND_MSG = "%s id not found, %s";
    private final EntityType type;
    protected final U repository;

    public BasicBaseService(U repository, EntityType type) {
        this.repository = repository;
        this.type = type;
    }

    private String getID_NOT_FOUND_MSG(UUID id) {
        return ID_NOT_FOUND_MSG.formatted(type, id);
    }

    @Override
    public T get(UUID id) {
        if (hasId(id)) {
            return repository.read(id);
        }
        throw new IllegalArgumentException(getID_NOT_FOUND_MSG(id));
    }

    @Override
    public <R extends Collection<UUID>> Map<UUID, T> getAll(R ids) {
        return repository.readMany(ids);
    }

    @Override
    public void update(UUID id, String newValue) {
        T entity = get(id);
        entity.update(newValue);
        repository.delete(id);
        repository.write(entity);
    }

    @Override
    public void delete(UUID id) {
        if (!hasId(id)) {
            throw new IllegalArgumentException(getID_NOT_FOUND_MSG(id));
        }
        repository.delete(id);
    }

    @Override
    public boolean hasId(UUID id) {
        return repository.hasId(id);
    }
}
