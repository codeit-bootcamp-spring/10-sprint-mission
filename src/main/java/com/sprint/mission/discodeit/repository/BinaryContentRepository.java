package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent find(UUID contentID);
    BinaryContent findAll();
    void save(BinaryContent binaryContent);
    void delete(UUID contentID);
}
