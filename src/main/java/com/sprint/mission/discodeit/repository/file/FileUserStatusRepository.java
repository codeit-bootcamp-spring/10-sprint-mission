package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "userStatus");

    public FileUserStatusRepository() {
        FileUtil.init(directory);
    }

    // 사용자 상태 저장
    @Override
    public void save(UserStatus userStatus) {
        Path filePath = Paths.get(directory.toString(), userStatus.getId() + ".ser");
        FileUtil.save(filePath, userStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public Optional<UserStatus> findById(UUID userStatusId) {
        UserStatus userStatus = FileUtil.loadSingle(directory.resolve(userStatusId + ".ser"));

        return Optional.ofNullable(userStatus);
    }

    // 사용자 상태 전체 조회
    @Override
    public List<UserStatus> findAll() {
        return FileUtil.load(directory);
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UserStatus userStatus) {
        try {
            Files.deleteIfExists(directory.resolve(userStatus.getId() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }

    // 유효성 검증 (중복 확인)
    @Override
    public boolean existsById(UUID userId) {
        return findAll().stream()
                .anyMatch(userStatus ->  userStatus.getUserId().equals(userId));
    }
}
