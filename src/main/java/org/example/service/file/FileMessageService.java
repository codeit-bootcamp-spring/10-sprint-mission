package org.example.service.file;

import org.example.entity.Channel;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.exception.InvalidRequestException;
import org.example.exception.NotFoundException;
import org.example.service.ChannelService;
import org.example.service.MessageService;
import org.example.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private static final Path FILE_PATH = Paths.get("data", "messages.ser");
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        initializeFile();
    }

    // ============================================
    // 파일 I/O 기본 구조
    // ============================================

    private void initializeFile() {
        try {
            if (Files.notExists(FILE_PATH.getParent())) {
                Files.createDirectories(FILE_PATH.getParent());
            }
            if (Files.notExists(FILE_PATH)) {
                saveToFile(new HashMap<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 초기화 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadFromFile() {
        if (Files.notExists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FILE_PATH))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 데이터 로드 실패", e);
        }
    }

    private void saveToFile(Map<UUID, Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("메시지 데이터 저장 실패", e);
        }
    }

    // ============================================
    // CRUD (구현 필요)
    // ============================================

    @Override
    public Message create(String content, UUID senderId, UUID channelId) {
        if (content == null || content.isBlank()) {
            throw new InvalidRequestException("content", "null이 아니고 빈 값이 아님", content);
        }
        User sender = userService.findById(senderId);
        Channel channel = channelService.findById(channelId);
        Message message = new Message(content, sender, channel);

        Map<UUID, Message> data = loadFromFile();
        data.put(message.getId(), message);
        saveToFile(data);

        message.addToChannelAndUser();

        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        Map<UUID, Message> data = loadFromFile();
        return Optional.ofNullable(data.get(messageId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 메시지", messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {
        // 채널 존재 여부 검증
        channelService.findById(channelId);

        return loadFromFile().values().stream()
                .filter(message -> !message.isDeletedAt())
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySender(UUID senderId) {
        // 유저 존재 여부 검증
        userService.findById(senderId);

        return loadFromFile().values().stream()
                .filter(message -> !message.isDeletedAt())
                .filter(message -> message.getSender().getId().equals(senderId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID messageId, String content) {
        Map<UUID, Message> data = loadFromFile();
        Message message = Optional.ofNullable(data.get(messageId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 메시지", messageId));

        message.updateContent(content);
        message.updateEditedAt(true);

        saveToFile(data);
        return message;
    }

    @Override
    public void softDelete(UUID messageId) {
        Map<UUID, Message> data = loadFromFile();
        Message message = Optional.ofNullable(data.get(messageId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 메시지", messageId));

        message.updateDeletedAt(true);

        saveToFile(data);
    }

    @Override
    public void hardDelete(UUID messageId) {
        Map<UUID, Message> data = loadFromFile();
        Message message = Optional.ofNullable(data.get(messageId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 메시지", messageId));

        message.removeFromChannelAndUser();

        data.remove(messageId);
        saveToFile(data);
    }



}
