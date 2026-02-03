package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final List<BinaryContent> data = new ArrayList<>();

    @Override
    public void save(BinaryContent content) {
        data.removeIf(c -> c.getId().equals(content.getId()));
        data.add(content);
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return data.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return data.stream().filter(c -> ids.contains(c.getId())).toList();
    }
}