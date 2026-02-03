package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

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
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<UUID> findParticipantUserIdsByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .map(ReadStatus::getUserId)
                .toList();
    }

    @Override
    public void delete(UUID readStatusId) {
        data.remove(readStatusId);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> readStatusIdsToDelete = data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .toList();

        for (UUID readStatusId : readStatusIdsToDelete) {
            data.remove(readStatusId);
        }
    }

}
