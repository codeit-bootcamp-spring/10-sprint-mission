package org.example.repository.file;

import org.example.entity.Channel;
import org.example.repository.ChannelRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private static final Path FILE_PATH = Paths.get("data", "channels.ser");

    public FileChannelRepository() {
        initializeFile();
    }

    // ============================================
    // 파일 I/O
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
    private Map<UUID, Channel> loadFromFile() {
        if (Files.notExists(FILE_PATH)) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FILE_PATH))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 데이터 로드 실패", e);
        }
    }

    private void saveToFile(Map<UUID, Channel> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("채널 데이터 저장 실패", e);
        }
    }

    // ============================================
    // Repository 구현
    // ============================================

    @Override
    public Channel save(Channel channel) {
        Map<UUID, Channel> data = loadFromFile();
        data.put(channel.getId(), channel);
        saveToFile(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Map<UUID, Channel> data = loadFromFile();
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public void deleteById(UUID channelId) {
        Map<UUID, Channel> data = loadFromFile();
        data.remove(channelId);
        saveToFile(data);
    }

    @Override
    public boolean existsById(UUID channelId) {
        return loadFromFile().containsKey(channelId);
    }
}
