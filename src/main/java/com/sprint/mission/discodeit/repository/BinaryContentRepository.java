package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    Optional<BinaryContent> findByUserId(UUID userId);
    Optional<BinaryContent> findByMessageId(UUID messageId);
    List<BinaryContent> findAll();
    void save(BinaryContent binaryContent);
    void delete(UUID id);
}
