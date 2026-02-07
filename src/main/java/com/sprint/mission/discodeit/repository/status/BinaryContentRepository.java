package com.sprint.mission.discodeit.repository.status;

import com.sprint.mission.discodeit.entity.status.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    Optional<BinaryContent> findById(UUID id);
    List<BinaryContent> findAllByIdln(List<UUID> ids);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
