package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    Optional<BinaryContent> find(UUID contentID);
    List<BinaryContent> findAll();
    BinaryContent save(BinaryContent binaryContent);
    void delete(UUID contentID);
}
