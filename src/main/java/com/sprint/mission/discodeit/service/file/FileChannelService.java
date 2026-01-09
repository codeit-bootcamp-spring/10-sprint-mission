package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {


    private final File file;

    public FileChannelService(String path) {
        this.file = new File(path);
    }


    private void save(Map<UUID, Channel> data){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> load(){
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Channel create(Channel channel) {
        Map<UUID, Channel> data = load();
        data.put(channel.getId(), channel);
        save(data);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        Map<UUID, Channel> data = load();
        return data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return List.copyOf(load().values());
    }

    @Override
    public Channel update(Channel channel) {
        Map<UUID, Channel> data = load();

        Channel found = data.get(channel.getId());
        if (found == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        found.updateName(channel.getName());
        found.updatePrivate(channel.getIsPrivate());
        found.updateUpdatedAt(System.currentTimeMillis());

        data.put(channel.getId(), channel);
        save(data);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> data = load();
        data.remove(id);
        save(data);
    }

    @Override
    public void clear() {
        save(new HashMap<>());
    }

}
