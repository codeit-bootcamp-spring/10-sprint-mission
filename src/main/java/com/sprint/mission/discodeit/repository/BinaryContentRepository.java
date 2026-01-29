package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    void save(BinaryContent profile);

    void delete(UUID profileId);

    List<BinaryContent> loadAll();

    BinaryContent loadById(UUID profileId);
}
