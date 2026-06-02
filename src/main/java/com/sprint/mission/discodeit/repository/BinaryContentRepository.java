package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {
    List<BinaryContent> findAllByIdIn(Set<UUID> ids);
}
