package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    @Override
    public ReadStatus save(ReadStatus entity) {
        return null;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public List<ReadStatus> findAll() {
        return List.of();
    }

    @Override
    public boolean existsById(UUID readStatusId) {
        return false;
    }

    @Override
    public void deleteById(UUID readStatusId) {

    }
}
