package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;

public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data;

    public JCFBinaryContentRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(BinaryContent binaryContent) {
        this.data.put(binaryContent.getId(), binaryContent);
    }

    @Override
    public void delete(UUID binaryContentId) {
        this.data.remove(binaryContentId);
    }

    @Override
    public List<BinaryContent> loadAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public BinaryContent loadById(UUID binaryContentId) {
        return this.data.get(binaryContentId);
    }
}
