package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BinaryContentRepository {

    BinaryContent save(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID binaryContentId);

    BinaryContent findByMessageId(UUID messageId);

    List<BinaryContent> findAll();

    void deleteById(UUID binaryContentId);
}
