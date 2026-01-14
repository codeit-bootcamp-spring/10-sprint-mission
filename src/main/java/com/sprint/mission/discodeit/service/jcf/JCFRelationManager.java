package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Entity;
import com.sprint.mission.discodeit.model.RelationModel;
import com.sprint.mission.discodeit.service.RelationManager;

import java.util.List;
import java.util.UUID;

public class JCFRelationManager<T extends Entity<T>, U extends Entity<U>>
        implements RelationManager<T, U> {
    private final RelationModel<T, U> model;

    public JCFRelationManager() {
        this(new RelationModel<>());
    }

    public JCFRelationManager(RelationModel<T, U> model) {
        this.model = model;
    }

    @Override
    public void create(T entity1, U entity2) {
        UUID key = entity1.getUuid();
        UUID value = entity2.getUuid();
        if (!model.containsKey(key)) {
            model.initKey(key);
        }
        model.add(key, value);
    }

    @Override
    public List<UUID> read(T entity) {
        UUID key = entity.getUuid();
        if (model.containsKey(key)) {
            return model.get(key);
        }
        return List.of();
    }

    @Override
    public void update(T entity, U oldValue, U newValue) {
        delete(entity.getUuid(), oldValue.getUuid());
        create(entity, newValue);
    }

    @Override
    public void delete(UUID uuid1, UUID uuid2) {
        if (model.containsKey(uuid1)) {
            model.remove(uuid1, uuid2);
        }
    }

    @Override
    public void deleteKey(UUID key) {
        model.removeKey(key);
    }
}
