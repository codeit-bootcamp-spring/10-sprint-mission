package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return Optional.ofNullable(data.get(readStatusId));
    }

    @Override
    public List<ReadStatus> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        boolean isExists = data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId));

        return isExists;
    }

    @Override
    public void deleteById(UUID readStatusId) {
        data.remove(readStatusId);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(
                readStatus -> readStatus.getChannelId().equals(channelId)
        );
    }
}
