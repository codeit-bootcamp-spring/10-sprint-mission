package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    Optional<BinaryContent> findById(UUID id);
    Optional<BinaryContent> findByUserId(UUID userId);
    List<BinaryContent> findByMessageId(UUID messageId);
    List<UUID> findAll();
    void save(BinaryContent binaryContent);
    void delete(UUID id);
    void deleteByUserId(UUID userId);
    void deleteByMessageId(UUID messageId);
}
