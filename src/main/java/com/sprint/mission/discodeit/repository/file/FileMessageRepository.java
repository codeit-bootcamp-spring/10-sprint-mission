package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

    private static final Path dirPath = Paths.get(System.getProperty("user.dir") + "/data/messages");

    public FileMessageRepository() {
        init();
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("메시지 데이터 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public Message save(Message message) {
        writeToFile(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return findAll().stream()
                .filter(message -> message.getId().equals(messageId))
                .findAny();
    }

    @Override
    public List<Message> findAll() {
        if(!Files.exists(dirPath)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(dirPath)){
            return stream
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Message) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Message message) {
        File file = new File(dirPath.toFile(), message.getId().toString() + ".ser");
        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("메시지 파일 삭제 실패");
            }
        }
    }

    private void writeToFile(Message message) {
        File file = new File(dirPath.toFile(), message.getId().toString() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 데이터 저장 실패", e);
        }
    }
}
