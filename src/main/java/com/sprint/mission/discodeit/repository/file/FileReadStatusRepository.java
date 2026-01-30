package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements ReadStatusRepository {

    public FileReadStatusRepository() {
        super("ReadStatus.ser");
    }

    @Override
    public void save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> data = load();
        data.put(readStatus.getId(), readStatus);
        writeToFile(data);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = load();
        data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
        writeToFile(data);
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        Map<UUID, ReadStatus> data = load();
        return data.values().stream()
                .anyMatch(rs ->
                        rs.getUserId().equals(userId)
                                && rs.getChannelId().equals(channelId)
                );
    }

}
