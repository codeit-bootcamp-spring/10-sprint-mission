package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Entity;
import com.sprint.mission.discodeit.model.Model;
import com.sprint.mission.discodeit.service.ModelManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFModelManager<T extends Entity<T>> implements ModelManager<T> {
    private final Model<T> model;

    public JCFModelManager(Model<T> model) {
        this.model = model;
    }

    @Override
    public void create(T entity) {
        model.add(entity);
    }

    @Override
    public Optional<T> read(UUID uuid) {
        return Optional.ofNullable(model.get(uuid));
    }

    @Override
    public List<T> readAll(List<UUID> uuids) {
        return model.getAll(uuids);
    }

    @Override
    public List<T> readAll() {
        return model.getAll();
    }

    @Override
    public void update(UUID uuid, String value) {
        model.update(uuid, value);
    }

    @Override
    public void delete(UUID key) {
        model.remove(key);
    }
}
