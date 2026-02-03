package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    @Override
    public BinaryContent save(BinaryContent content) {
        return null;
    }

    @Override
    public Optional<BinaryContent> findById(UUID contentId) {
        return Optional.empty();
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> contentIds) {
        return List.of();
    }

    @Override
    public void delete(UUID contentId) {

    }
}
