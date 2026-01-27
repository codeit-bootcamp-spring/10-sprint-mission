package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data; // 빠른 조회를 위한 컬렉션
    private final Path messageDir;

    public FileMessageRepository(Path messageDir) {
        this.data = new HashMap<>();
        this.messageDir = messageDir;
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(messageDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromFiles();
    }

    private Path messageFilePath(UUID messageId) {
        // 메시지를 구분하기 위한 파일 경로 생성
        return messageDir.resolve("message-" + messageId +".ser");
    }

    @Override
    public Message saveMessage(Message message) {
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
    public Optional<Message> findMessageByMessageId(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteMessage(UUID messageId) {
        deleteFileAndRemoveFromData(messageId);
    }

    public Message loadMessageFile(UUID messageId) {
        // 경로 생성 (message-id.ser)
        Path filePath = messageFilePath(messageId);

        // 파일 존재 여부 확인
        if (!Files.exists(filePath)) {
            throw new RuntimeException("메시지 파일이 존재하지 않습니다.");
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // 파일 조회 후 컬렉션과 동기화
            Message message = (Message) ois.readObject();
            data.put(message.getId(), message);
            return message;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 파일 로딩을 실패했습니다.");
        }
    }

    private void loadAllFromFiles() {
        try {
            Files.list(messageDir)
                    .filter(path -> path.getFileName().toString().startsWith("message-")) // 파일명이 "message-"로 시작해야 함
                    .filter(path -> path.getFileName().toString().endsWith(".ser")) // 파일의 확장자가 ".ser"이어야 함
                    .forEach(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            // 파일 조회 후 컬렉션에 저장
                            Message message = (Message) ois.readObject();
                            data.put(message.getId(), message);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("메시지 파일 로딩을 실패했습니다.");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("메시지 디렉토리 조회를 실패했습니다.");
        }
    }

    private void deleteFileAndRemoveFromData(UUID messageId) {
        // 경로 생성 (message-id.ser)
        Path filePath = messageFilePath(messageId);

        try {
            // 파일이 존재한다면 삭제 후 true 반환
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw new RuntimeException("메시지가 존재하지 않습니다.");
            }
            // 컬렉션에서도 삭제
            data.remove(messageId);
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 삭제를 실패했습니다.");
        }
    }
}
