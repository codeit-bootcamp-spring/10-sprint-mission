package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileBinaryContentRepository extends BaseFileRepository<BinaryContent> implements BinaryContentRepository {
    public FileBinaryContentRepository() {
        super("contents.ser");
    }

    @Override
    public BinaryContent save(BinaryContent content) {
        Map<UUID, BinaryContent> data = loadData();
        data.put(content.getId(), content);
        saveData(data);
        return content;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, BinaryContent> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
