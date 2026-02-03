package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository(){
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus){
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id){
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId){
        return this.data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId){
        return this.data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID id){
        this.data.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId){
        List<UUID> idsToDelete = this.data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .toList();
        idsToDelete.forEach(this.data::remove);
    }

}
