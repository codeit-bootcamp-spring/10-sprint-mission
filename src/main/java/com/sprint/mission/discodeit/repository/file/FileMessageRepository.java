package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "file"
)
public class FileMessageRepository implements MessageRepository {
    private final Path directory;

    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String baseDirectory) {
        this.directory = Paths.get(System.getProperty("user.dir"), baseDirectory, "message");
        FileUtil.init(directory);
    }

    // 메시지 저장
    @Override
    public void save(MessageEntity message) {
        Path filePath = directory.resolve(message.getId() + ".ser");
        FileUtil.save(filePath, message);
    }

    // 메시지 단건 조회
    @Override
    public Optional<MessageEntity> findById(UUID messageId) {
        MessageEntity message = FileUtil.loadSingle(directory.resolve(messageId + ".ser"));

        return Optional.ofNullable(message);
    }

    // 메시지 다건 조회
    @Override
    public List<MessageEntity> findAll() {
        return FileUtil.load(directory);
    }

    // 메시지 삭제
    @Override
    public void delete(MessageEntity message) {
        try {
            Files.deleteIfExists(directory.resolve(message.getId() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다." + e);
        }
    }

    @Override
    public Instant getLastMessageAt(UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .max(Comparator.comparing(MessageEntity::getCreatedAt))
                .map(MessageEntity::getCreatedAt)
                .orElse(null);
    }
}
