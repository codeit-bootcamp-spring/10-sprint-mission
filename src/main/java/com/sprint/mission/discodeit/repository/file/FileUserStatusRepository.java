package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements UserStatusRepository {

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory:.discodeit}")String directoryPath) {
        super(directoryPath + File.separator + "UserStatus.ser");
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        Map<UUID, UserStatus> data = load();
        Optional<UserStatus> userStatus = data.values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findAny();
        return userStatus;
    }

    @Override
    public Map<UUID, UserStatus> getUserStatusMap() {
        Map<UUID, UserStatus> data = load();
        return data.values().stream()
                .collect(Collectors.toMap(UserStatus::getUserId, us -> us));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Map<UUID, UserStatus> data = load();
        Optional<UserStatus> userStatus = data.values().stream()
                .filter(us -> us.getUserId().equals(userId))
                .findAny();
        userStatus.ifPresent(us -> {
            data.remove(us.getUserId());
            writeToFile(data);
        });
    }

}
