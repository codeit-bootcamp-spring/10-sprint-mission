package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
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
    private final Path directory;

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String baseDirectory) {
        this.directory = Paths.get(System.getProperty("user.dir"), baseDirectory, "userStatus");
        FileUtil.init(directory);
    }

    // 사용자 상태 저장
    @Override
    public void save(UserStatusEntity userStatus) {
        Path filePath = Paths.get(directory.toString(), userStatus.getId() + ".ser");
        FileUtil.save(filePath, userStatus);
    }

    // 사용자 상태 단건 조회
    @Override
    public Optional<UserStatusEntity> findById(UUID userStatusId) {
        UserStatusEntity userStatus = FileUtil.loadSingle(directory.resolve(userStatusId + ".ser"));

        return Optional.ofNullable(userStatus);
    }

    // 특정 사용자의 상태 단건 조회
    @Override
    public UserStatusEntity findByUserId(UUID userId) {
        return findAll().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 상태가 존재하지 않습니다."));
    }

    // 사용자 상태 전체 조회
    @Override
    public List<UserStatusEntity> findAll() {
        return FileUtil.load(directory);
    }

    // 사용자 상태 삭제
    @Override
    public void delete(UserStatusEntity userStatus) {
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
