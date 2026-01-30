package com.sprint.mission.discodeit.binaryContent.repository;

import com.sprint.mission.discodeit.binaryContent.model.BinaryContent;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID id);

    void deleteById(UUID id);
}
