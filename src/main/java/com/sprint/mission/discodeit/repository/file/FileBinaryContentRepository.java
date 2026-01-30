package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        return null;
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.of();
    }

    @Override
    public void delete(BinaryContent binaryContent) {

    }
}
