package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileBinaryContentRepository extends BaseFileRepository<BinaryContent> implements BinaryContentRepository {
    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        super(directory + "/binary_contents.ser");
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
    public List<BinaryContent> findAllById(Iterable<UUID> ids) {
        Map<UUID, BinaryContent> data = loadData();
        List<BinaryContent> result = new ArrayList<>();
        for (UUID id : ids) {
            BinaryContent content = data.get(id);
            if (content != null) {
                result.add(content);
            }
        }
        return result;
    }

    @Override
    public List<BinaryContent> findAll() {
        return loadData().values().stream().toList();
    }

    @Override
    public void deleteById(UUID id) {
        Map<UUID, BinaryContent> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
