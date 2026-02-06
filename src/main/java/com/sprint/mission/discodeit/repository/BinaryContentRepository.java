package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface BinaryContentRepository {
    Optional<BinaryContent> findById(UUID id);
    void save(BinaryContent binaryContent);
    void deleteById(UUID id);
}
