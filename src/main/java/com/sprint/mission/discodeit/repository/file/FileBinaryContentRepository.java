package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> data;
    private final FileObjectStore fileObjectStore;

    public FileBinaryContentRepository(FileObjectStore fileObjectStore) {
        this.data = fileObjectStore.getBinaryContentsData();
        this.fileObjectStore = fileObjectStore;
    }

    @Override
    public void save(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        fileObjectStore.saveData();
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
        fileObjectStore.saveData();
    }
}
