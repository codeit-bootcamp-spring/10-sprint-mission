package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
@ConditionalOnProperty(name ="discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        return data.put(readStatus.getId(), readStatus);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values()
                .stream()
                .filter(s->s.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.values()
                .stream()
                .filter(s->s.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.values()
                .stream()
                .filter(s->s.getUserId().equals(userId) && s.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> find(UUID readStatusId) {
        return Optional.ofNullable(this.data.get(readStatusId));
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(s->s.getChannelId().equals(channelId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.values().removeIf(s->s.getUserId().equals(userId));
    }

    @Override
    public void delete(UUID readStatusId) {
        data.remove(readStatusId);
    }
}
