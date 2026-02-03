package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> storage = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) { storage.put(readStatus.getId(), readStatus); }

    @Override
    public Optional<ReadStatus> findById(UUID id) { return Optional.ofNullable(storage.get(id)); }

    @Override
    public void deleteById(UUID id) { storage.remove(id); }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.values().stream()
                .filter(r -> r.getUserId().equals(userId) && r.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return storage.values().stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return storage.values().stream().filter(r -> r.getChannelId().equals(channelId)).toList();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        storage.values().removeIf(r -> r.getChannelId().equals(channelId));
    }
}