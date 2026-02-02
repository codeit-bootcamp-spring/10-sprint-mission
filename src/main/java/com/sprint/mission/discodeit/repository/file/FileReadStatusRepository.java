package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;
import org.springframework.stereotype.Repository;

import javax.imageio.IIOException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "readStatus");

    public FileReadStatusRepository() {
        FileUtil.init(directory);
    }

    // 읽음 상태 저장
    @Override
    public void save(ReadStatus readStatus) {
        Path filePath = Paths.get(directory.toString(),readStatus.getId() + ".ser");
        FileUtil.save(filePath, readStatus);
    }

    // 읽음 상태 단건 조회
    @Override
    public Optional<ReadStatus> findById(UUID readStatusId) {
        ReadStatus readStatus = FileUtil.loadSingle(directory.resolve(readStatusId + ".ser"));

        return Optional.ofNullable(readStatus);
    }

    // 읽음 상태 전체 조회
    @Override
    public List<ReadStatus> findAll() {
        return FileUtil.load(directory);
    }

    // 사용자 삭제
    @Override
    public void delete(ReadStatus readStatus) {
        try {
            Files.deleteIfExists(directory.resolve(readStatus.getId() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }

    // 유효성 검증 (중복 확인)
    @Override
    public Boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return findAll().stream()
                .anyMatch(readStatus -> readStatus.getUserId().equals(userId) &&
                        readStatus.getChannelId().equals(channelId));
    }
}
