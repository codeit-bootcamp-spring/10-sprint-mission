package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final File file = new File("channels.dat");
    private Map<UUID, Channel> channelMap;

    public FileChannelRepository() {
        if (file.exists()) {
            load();
        } else {
            this.channelMap = new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.channelMap = (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("데이터 로드 중 오류 발생" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.channelMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        saveFile();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void delete(UUID id) {
        channelMap.remove(id);
        saveFile();
    }

}