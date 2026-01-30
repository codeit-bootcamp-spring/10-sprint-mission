package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReadStatusRepositoryImpl implements ReadStatusRepository{
    @Override
    public ReadStatus save(ReadStatus readStatus) {
        return null;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return null;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.empty();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public List<ReadStatus> findAll() {
        return List.of();
    }

    @Override
    public void delete(ReadStatus readStatus) {

    }
}
