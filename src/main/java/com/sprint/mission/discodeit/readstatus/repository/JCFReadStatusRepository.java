package com.sprint.mission.discodeit.readstatus.repository;

import com.sprint.mission.discodeit.readstatus.entity.ReadStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> data = new ArrayList<>();

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return data.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return List.copyOf(data);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void save(ReadStatus readStatus) {
        if(data.contains(readStatus))
            data.set(data.indexOf(readStatus), readStatus);
        else
            data.add(readStatus);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(rs -> rs.getId().equals(id));
    }
}
