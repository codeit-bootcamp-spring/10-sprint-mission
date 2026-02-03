package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> data = new ArrayList<>();

    @Override
    public void save(ReadStatus readStatus) {
        data.removeIf(r -> r.getId().equals(readStatus.getId()));
        data.add(readStatus);
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return data.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(r -> r.getId().equals(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.stream()
                .filter(r -> r.getUserId().equals(userId) && r.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.stream().filter(r -> r.getChannelId().equals(channelId)).toList();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.removeIf(r -> r.getChannelId().equals(channelId));
    }
}