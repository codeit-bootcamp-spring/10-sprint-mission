package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ChannelService AND ChannelRepository 모두 구현하고, ChannelRepository 빈으로 등록
@Primary  // BasicMessageService에서 ChannelRepository 빈을 자동 주입할 수 있도록
@Service
public class FileChannelService implements ChannelService, ChannelRepository {

    private final List<Channel> data = new ArrayList<>();
    private final Path filePath;

    public FileChannelService() {
        this.filePath = Path.of("data", "channels.ser");
        load();
    }

    private void load() {
        if (Files.notExists(filePath)) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<Channel> loaded = (List<Channel>) ois.readObject();
            data.clear();
            data.addAll(loaded);
        } catch (InvalidClassException e) {
            System.out.println("이전 버전 파일을 무시하고 새로 시작합니다: " + e.getMessage());
            data.clear();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ChannelService 인터페이스 메서드 (기존 그대로, 내부 저장소 data를 사용)
    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        save();
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = find(channelId);
        Optional.ofNullable(newName).ifPresent(channel::updateChannelName);
        Optional.ofNullable(newDescription).ifPresent(channel::updateChannelDescription);
        channel.touch();
        save();
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = find(channelId);
        data.remove(channel);
        save();
    }

    // ChannelRepository 인터페이스 메서드
    @Override
    public Channel save(Channel channel) {
        // Channel이 이미 저장소에 있으면 update(이미 ChannelService에서 touch/save 처리),
        // 없으면 add 후 저장
        boolean exists = data.removeIf(c -> c.getId().equals(channel.getId()));
        if (!exists) {
            data.add(channel);
        } else {
            // 이미 존재하는 경우, touch()가 이미 업데이트 된 상태일 수 있으므로 바로 저장
        }
        save();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
        save();
    }
}
