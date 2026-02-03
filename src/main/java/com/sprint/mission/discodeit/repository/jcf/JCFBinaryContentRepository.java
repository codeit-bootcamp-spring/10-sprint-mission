package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> storage = new HashMap<>();

    @Override
    public void save(BinaryContent content) { storage.put(content.getId(), content); }

    @Override
    public Optional<BinaryContent> findById(UUID id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public void deleteById(UUID id) { storage.remove(id); }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return storage.values().stream().filter(c -> ids.contains(c.getId())).toList();
    }
}