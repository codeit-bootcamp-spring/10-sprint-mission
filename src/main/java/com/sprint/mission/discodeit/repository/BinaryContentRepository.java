package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    void save(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID profileId);

    void delete(BinaryContent binaryContent);

    void deleteById(UUID profileId);
}
