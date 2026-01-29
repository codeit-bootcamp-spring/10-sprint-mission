package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    BinaryContent findById(UUID id);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    BinaryContent findByUserId(UUID userId);
    List<BinaryContent> findAllByMessageId(UUID messageId);
    void deleteById(UUID id);
}