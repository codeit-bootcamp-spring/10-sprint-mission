package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements ReadStatusRepository {

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}")String directoryPath) {
        super(directoryPath + File.separator + "ReadStatus.ser");
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = load();
        if(data.values().removeIf(rs -> rs.getChannelId().equals(channelId))){
            writeToFile(data);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = load();
        if(data.values().removeIf(rs -> rs.getUserId().equals(userId))) {
            writeToFile(data);
        }
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


    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = load();
        return data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

}
