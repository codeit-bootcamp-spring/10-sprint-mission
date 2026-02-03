package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent content);
    Optional<BinaryContent> findById(UUID contentId);
    List<BinaryContent> findAllByIdIn(List<UUID> contentIds);
    void delete(UUID contentId);
}
