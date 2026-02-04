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
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> readStatusList;

    public JCFReadStatusRepository() {
     readStatusList = new ArrayList<>();
    }

    @Override
    public Optional<ReadStatus> find(UUID id) {
        return readStatusList.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findByUserID(UUID userID) {
        return readStatusList.stream()
                .filter(rs -> rs.getUserID().equals(userID))
                .toList();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatusList);
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatusList.removeIf(rs -> rs.getId().equals(readStatus.getId()));
        readStatusList.add(readStatus);
        return readStatus;
    }

    @Override
    public void delete(UUID readStatusID) {
        readStatusList.removeIf(rs -> rs.getId().equals(readStatusID));
    }

    @Override
    public void deleteByChannelID(UUID channelID) {
        readStatusList.removeIf(rs -> rs.getChannelID().equals(channelID));
    }

    @Override
    public void deleteByChannelIDAndUserID(UUID channelID, UUID userID){
        readStatusList.removeIf(rs -> rs.getChannelID().equals(channelID) && rs.getUserID().equals(userID));
    }
}
