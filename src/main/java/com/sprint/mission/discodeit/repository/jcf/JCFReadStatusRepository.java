package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final List<ReadStatus> data = new ArrayList<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        delete(readStatus);
        data.add(readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.stream()
                .filter(status -> status.getUserId().equals(userId))
                .filter(status -> status.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        return data.stream()
                .filter(status -> status.getId().equals(readStatusId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.stream()
                .filter(status -> status.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void delete(ReadStatus readStatus) {
        data.removeIf(status -> status.equals(readStatus));
    }
}
