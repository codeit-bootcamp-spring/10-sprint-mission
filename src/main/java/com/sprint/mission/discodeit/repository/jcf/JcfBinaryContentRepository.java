package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JcfBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> store = new LinkedHashMap<>();

    @Override
    public synchronized BinaryContent save(BinaryContent content) {
        store.put(content.getId(), content);
        return content;
    }

    @Override
    public synchronized BinaryContent findById(UUID id) {
        return store.get(id);
    }

    @Override
    public synchronized List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null) return List.of();
        return ids.stream()
                .map(store::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public synchronized void delete(UUID id) {
        store.remove(id); // 없으면 그냥 무시
    }
}
