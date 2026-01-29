package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
        // [변경] user.dir 기반으로 맥/윈도우 공용 경로 설정
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),
                "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("메시지 디렉토리 생성 실패", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Message save(Message message) {
        Path path = resolvePath(message.getId());
        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패", e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return readMessageFromFile(resolvePath(id));
    }

    @Override
    public List<Message> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readMessageFromFile)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패", e);
        }
    }

    // [헬퍼 메서드] findId, findAll: 중복되는 역직렬화 로직 통합
    private Optional<Message> readMessageFromFile(Path path) {
        if (Files.notExists(path)) return Optional.empty();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.ofNullable((Message) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
