package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Profile("file")
public class FileReadStatusRepository extends BaseFileRepository<ReadStatus> implements ReadStatusRepository {
    public FileReadStatusRepository() {super("read_statuses.ser");}

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        Map<UUID, ReadStatus> data = loadData();
        data.put(readStatus.getId(), readStatus);
        saveData(data);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id){return Optional.ofNullable(loadData().get(id));}

    // 특정 유저의 특정 채널 읽음 상태 조회
    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    // 특정 유저가 참여 중인 모든 채널의 읽음 상태 조회
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        Map<UUID, ReadStatus> data = loadData();
        return data.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    // 특정 채널에 참여 중인 모든 유저의 읽음 상태 조회
    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();
        return data.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID id){
        Map<UUID, ReadStatus> data = loadData();
        data.remove(id);
        saveData(data);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        Map<UUID, ReadStatus> data = loadData();
        data.values().removeIf(readStatus -> readStatus.getChannelId().equals(channelId));
        saveData(data);
    }
}
