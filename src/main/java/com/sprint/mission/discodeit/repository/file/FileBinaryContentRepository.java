package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.extend.FileSerDe;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository extends FileSerDe<BinaryContent> implements BinaryContentRepository {
    private final String BINARY_CONTENT_DATA_DIRECTORY = "data/binarycontent";

    public FileBinaryContentRepository() {
        super(BinaryContent.class);
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        return super.save(BINARY_CONTENT_DATA_DIRECTORY, binaryContent);
    }

    @Override
    public Optional<BinaryContent> findById(UUID uuid) {
        return super.load(BINARY_CONTENT_DATA_DIRECTORY, uuid);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> uuids) {
        return uuids.stream()
                .map(this::findById)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        super.delete(BINARY_CONTENT_DATA_DIRECTORY, uuid);
    }
}
