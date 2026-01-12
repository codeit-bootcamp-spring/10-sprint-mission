package com.sprint.mission.discodeit.model;

import com.sprint.mission.discodeit.entity.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RelationModel<T extends Entity<T>, U extends Entity<U>> {
    private final Map<UUID, List<UUID>> data;

    public RelationModel() {
        this(new HashMap<>());
    }

    public RelationModel(Map<UUID, List<UUID>> data) {
        this.data = data;
    }

    public boolean contain(UUID key) {
        return data.containsKey(key);
    }

    public void add(UUID key, UUID value) {
        get(key, false).add(value);
    }

    public void remove(UUID key, UUID value) {
        data.get(key).remove(value);
    }

    private List<UUID> get(UUID key, boolean isCopy) {
        if (isCopy) {
            return get(key);
        }
        return data.get(key);
    }

    public List<UUID> get(UUID key) {
        return get(key, true);
    }
}
