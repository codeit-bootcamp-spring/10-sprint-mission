package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(readStatus.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), readStatus);
        } else {
            data.add(readStatus);
        }

        return readStatus;
    }

    @Override
    public List<ReadStatus> saveAll(List<ReadStatus> readStatus) {
        for (ReadStatus status : readStatus) {
            save(status);
        }
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return data.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(rs -> rs.getId().equals(id));
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        data.removeIf(rs -> rs.getChannelId().equals(channelId));
    }
}
