package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class BinaryContentRepositoryTemp implements BinaryContentRepository {

    @Override
    public BinaryContent save(BinaryContent content) {
        return null;
    }

    @Override
    public Optional<BinaryContent> findById(UUID contentId) {
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID contentId) {

    }
}
