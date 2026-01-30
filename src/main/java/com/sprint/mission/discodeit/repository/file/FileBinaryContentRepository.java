package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileBinaryContentRepository extends AbstractFileRepository<BinaryContent> implements BinaryContentRepository {

    protected FileBinaryContentRepository() {
        super("BinaryContent.ser");
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Map<UUID, BinaryContent> data = load();
        data.put(binaryContent.getId(), binaryContent);
        writeToFile(data);
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, BinaryContent> data = load();
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        Map<UUID, BinaryContent> data = load();
        return data.values().stream()
                .filter(bc -> bc.getId().equals(id))
                .findFirst();
    }

}
