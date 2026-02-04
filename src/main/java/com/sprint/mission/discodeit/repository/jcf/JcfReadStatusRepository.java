package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.DuplicationReadStatusException;
import com.sprint.mission.discodeit.exception.StatusNotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JcfReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> readStatuses = new LinkedHashMap<>();

    public void reset() {
        readStatuses.clear();
    }

    @Override
    public synchronized ReadStatus save(ReadStatus readStatus) {
        boolean duplicate = readStatuses.values().stream().anyMatch(existing ->
                existing.getUserId().equals(readStatus.getUserId())
                        && existing.getChannelId().equals(readStatus.getChannelId())
                        && !existing.getId().equals(readStatus.getId())
        );
        if (duplicate) throw new DuplicationReadStatusException();

        readStatuses.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public synchronized ReadStatus findById(UUID id) {
        return readStatuses.get(id);
    }

    @Override
    public synchronized List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : readStatuses.values()) {
            if (rs.getUserId().equals(userId)) result.add(rs);
        }
        return result;
    }

    @Override
    public synchronized ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus rs : readStatuses.values()) {
            if (rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId)) {
                return rs;
            }
        }
        return null;
    }

    @Override
    public synchronized void delete(UUID id) {
        if (readStatuses.remove(id) == null) {
            throw new StatusNotFoundException();
        }
    }

    @Override
    public synchronized void deleteByChannelId(UUID channelId) {
        readStatuses.entrySet().removeIf(e -> e.getValue().getChannelId().equals(channelId));
    }

    @Override
    public synchronized void deleteByUserId(UUID userId) {
        readStatuses.entrySet().removeIf(e -> e.getValue().getUserId().equals(userId));
    }
}
