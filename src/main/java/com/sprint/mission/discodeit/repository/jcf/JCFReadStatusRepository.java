package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDTO;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
    }

    @Override
    public void delete(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        for (ReadStatus readStatus : this.data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                this.data.remove(readStatus.getId());
            }
        }
    }

    @Override
    public List<ReadStatus> loadAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public ReadStatus loadById(UUID readStatusId) {
        return this.data.get(readStatusId);
    }

    @Override
    public List<ReadStatus> loadAllByChannelId(UUID channelId) {
        List<ReadStatus> readStatusList = new ArrayList<>();
        for (ReadStatus readStatus : this.data.values()) {
            if (readStatus.getChannelId().equals(channelId)) {
                readStatusList.add(readStatus);
            }
        }
        return readStatusList;
    }
}
