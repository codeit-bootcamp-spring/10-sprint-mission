package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileUserStatusRepository extends BaseFileRepository<UserStatus> implements UserStatusRepository {
    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        super(directory + "/user_status.ser");
    }

    @Override
    public UserStatus save(UserStatus status) {
        Map<UUID, UserStatus> data = loadData();
        data.put(status.getId(), status);
        saveData(data);
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {return Optional.ofNullable(loadData().get(id));}

    // 특정 유저의 접속 상태 조회
    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return loadData().values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() { return loadData().values().stream().toList();}

    @Override
    public void deleteById(UUID id) {
        Map<UUID, UserStatus> data = loadData();
        data.remove(id);
        saveData(data);
    }

    // 유저 삭제 시 해당 유저의 접속 상태 데이터 삭제
    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = loadData();
        data.values().removeIf(us -> us.getUserId().equals(userId));
        saveData(data);
    }
}
