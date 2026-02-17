package com.sprint.mission.discodeit.repository.jcf;


import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> readStatusData = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) {
        if (readStatus == null || readStatus.getId() == null) {
            throw new IllegalArgumentException("readStatus/id는 null일 수 없습니다.");
        }
        readStatusData.put(readStatus.getId(), readStatus);
    }


    @Override
    public void delete(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null 일수 없습니다.");
        readStatusData.remove(id);
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatusData.values());
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");
        return Optional.ofNullable(readStatusData.get(id));
    }


    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : readStatusData.values()) {
            if (channelId.equals(rs.getChannelId())) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }

        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : readStatusData.values()) {
            if (userId.equals(rs.getUserId())) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        for (ReadStatus rs : readStatusData.values()) {
            if (userId.equals(rs.getUserId()) && channelId.equals(rs.getChannelId())) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }
}



