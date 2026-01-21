package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final String FILE_PATH = "channels.dat";
    private final Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = loadFromFile();
    }

    @Override
    public Channel create(String name, String description) {
        Objects.requireNonNull(name, "채널 이름은 필수 항목입니다.");
        Objects.requireNonNull(description, "채널 설명은 필수 항목입니다.");

        Channel channel = new Channel(name, description);

        data.put(channel.getId(), channel);
        saveToFile();

        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        findById(channelId);

        data.remove(channelId);
        saveToFile();
    }

    @Override
    public Channel findById(UUID id) {
        Objects.requireNonNull(id, "채널 Id가 유효하지 않습니다.");
        Channel channel = data.get(id);

        return Objects.requireNonNull(channel, "Id에 해당하는 채널이 존재하지 않습니다.");
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        Channel channel = findById(channelId);

        Optional.ofNullable(name).ifPresent(channel::updateName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);

        saveToFile();

        return channel;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("채널 데이터 저장 중 오류 발생: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadFromFile() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("채널 데이터 로드 중 오류 발생: " + e.getMessage());
            return new HashMap<>();
        }
    }
}