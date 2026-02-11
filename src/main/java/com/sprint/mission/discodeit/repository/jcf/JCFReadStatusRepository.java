package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID uuid) {
        return Optional.ofNullable(this.data.get(uuid));
    }

    @Override
    public List<ReadStatus> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return this.data.values().stream()
                .filter(rs -> Objects.equals(rs.getUserId(), userId))
                .toList();
    }

    @Override
    public void deleteById(UUID uuid) {
        this.data.remove(uuid);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        this.data.values()
                .removeIf(rs -> Objects.equals(rs.getChannelId(), channelId));
    }
}
