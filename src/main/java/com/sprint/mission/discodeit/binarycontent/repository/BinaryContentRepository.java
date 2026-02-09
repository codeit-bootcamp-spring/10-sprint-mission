package com.sprint.mission.discodeit.binarycontent.repository;

import com.sprint.mission.discodeit.binarycontent.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    Optional<BinaryContent> findById(UUID id);
    List<BinaryContent> findAll();
    void save(BinaryContent binaryContent);
    void deleteById(UUID id);
}
