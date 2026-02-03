package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.SerializedFileUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PREFIX = "message";
    private static final String ENTITY_NAME = "메시지";

    private final Map<UUID, Message> data; // 빠른 조회를 위한 컬렉션
    private final Path messageDir;

    public FileMessageRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.messageDir = baseDir.resolve(FILE_PREFIX);
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(messageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromDirectory();
    }

    private Path messageFilePath(UUID messageId) {
        // 메시지를 구분하기 위한 파일 경로 생성
        return messageDir.resolve(FILE_PREFIX + "-" + messageId + ".ser");
    }

    @Override
    public Message save(Message message) {
        // 경로 생성 (message-id.ser)
        Path filePath = messageFilePath(message.getId());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // 파일 저장
            oos.writeObject(message);
            data.put(message.getId(), message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 저장을 실패했습니다.");
        }
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .toList();
    }

    @Override
    public Optional<Instant> findLatestMessageTimeByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID messageId) {
        if (!data.containsKey(messageId)) {
            throw new RuntimeException("메시지가 존재하지 않습니다.");
        }

        Path filePath = messageFilePath(messageId);
        SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
        data.remove(messageId);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> messageIdsToDelete = data.values().stream()
                        .filter(message -> channelId.equals(message.getChannelId()))
                        .map(Message::getId)
                        .toList();

        for (UUID messageId : messageIdsToDelete) {
            Path filePath = messageFilePath(messageId);
            SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
            data.remove(messageId);
        }
    }

    public Message loadByIdFromFile(UUID messageId) {
        // 경로 생성 (message-id.ser)
        Path filePath = messageFilePath(messageId);
        // 파일 역직렬화
        Message message = (Message) SerializedFileUtils.deserialize(filePath, ENTITY_NAME);
        // 컬렉션과 동기화
        data.put(message.getId(), message);
        return message;
    }

    private void loadAllFromDirectory() {
        data.clear();

        for (Object object : SerializedFileUtils.deserializeAll(messageDir, FILE_PREFIX, ENTITY_NAME)) {
            Message message = (Message) object;
            data.put(message.getId(), message);
        }
    }
}
