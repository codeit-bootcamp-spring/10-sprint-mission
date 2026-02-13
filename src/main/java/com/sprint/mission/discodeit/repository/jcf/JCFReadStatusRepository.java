package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return this.data.values().stream()
                .filter(readStatus ->
                        readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        return this.data.get(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return this.data.values().stream()
                .filter(readStatus ->readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public void delete(UUID readStatusId) {
        data.remove(readStatusId);

    }

    @Override
    public boolean existsById(UUID userId, UUID channelId) {
        return this.data.values().stream()
                .anyMatch(readStatus ->
                        readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId));
    }

    @Override
    public void deleteById(UUID userId, UUID channelId) {
        this.data.values().removeIf(readStatus ->
                readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId));

    }

    @Override
    public List<UUID> findUserIdsByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(readStatus ->readStatus.getChannelId().equals(channelId))
                .map(ReadStatus::getUserId)
                .toList();
    }

    @Override
    public List<UUID> findChannelIdsByUserId(UUID userId) {
        return data.values().stream()
                .filter(readStatus ->readStatus.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .toList();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        this.data.values().removeIf(readStatus ->
                readStatus.getChannelId().equals(channelId));

    }
}
