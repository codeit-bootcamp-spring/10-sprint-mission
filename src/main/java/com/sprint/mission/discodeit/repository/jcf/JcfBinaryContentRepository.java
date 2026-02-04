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

    private final Map<UUID, BinaryContent> binaryContents = new LinkedHashMap<>();

    // 선택: 테스트/메인 편의용
    public void reset() {
        binaryContents.clear();
    }

    @Override
    public synchronized BinaryContent save(BinaryContent content) {
        binaryContents.put(content.getId(), content);
        return content;
    }

    @Override
    public synchronized BinaryContent findById(UUID id) {
        return binaryContents.get(id);
    }

    @Override
    public synchronized List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null) return List.of();

        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent found = binaryContents.get(id);
            if (found != null) result.add(found);
        }
        return result;
    }

    @Override
    public synchronized void delete(UUID id) {
        binaryContents.remove(id);
    }
}
