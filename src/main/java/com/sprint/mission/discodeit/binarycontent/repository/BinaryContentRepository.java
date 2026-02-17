package com.sprint.mission.discodeit.binarycontent.repository;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;

import java.util.Optional;
import java.util.UUID;


public interface BinaryContentRepository {
    Optional<BinaryContent> findById(UUID id);
    void save(BinaryContent binaryContent);
    void deleteById(UUID id);
}
