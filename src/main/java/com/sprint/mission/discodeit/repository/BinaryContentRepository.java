package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public interface BinaryContentRepository {
    void save(UUID id, BinaryContent binaryContent);
    void deleteByUserId(UUID userId);
}
