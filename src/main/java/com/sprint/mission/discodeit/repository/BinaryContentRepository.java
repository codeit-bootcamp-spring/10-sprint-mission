package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    Optional<BinaryContent> findById(UUID id);
    List<BinaryContent> findAll();
    List<BinaryContent> findAllByProfileId(UUID profileId);
    List<BinaryContent> findAllAttachmentId(UUID attachmentId);
    BinaryContent save(BinaryContent binaryContent);
    void deleteById(UUID id);
}
