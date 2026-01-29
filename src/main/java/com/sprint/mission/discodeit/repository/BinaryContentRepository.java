package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    void save(BinaryContent content);
    Optional<BinaryContent> findById(UUID id);
    void delete(UUID id);
}
