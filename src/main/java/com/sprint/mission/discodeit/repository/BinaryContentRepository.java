package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);
    BinaryContent findById(UUID binaryContentId);
    List<BinaryContent> findAll();
    void delete(BinaryContent binaryContent);
}
