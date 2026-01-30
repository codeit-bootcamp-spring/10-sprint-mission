package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return Optional.ofNullable(data.get(binaryContentId));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID binaryContentId) {
        data.remove(binaryContentId);
    }
}
