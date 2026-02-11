package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    List<byte[]> findAllImagesByIds(List<UUID> attachmentIds);

    List<BinaryContent> findAllByIds(List<UUID> ids);

    void save(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID profileId);

    void delete(BinaryContent binaryContent);

    void deleteById(UUID profileId);
}
