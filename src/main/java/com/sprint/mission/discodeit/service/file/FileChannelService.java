package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService, ClearMemory {

    private final File file;

    public FileChannelService(String path) {
        this.file = new File(path);
    }

    private void save(Channel channel) {
        Map<UUID, Channel> data = load();

        if(data.containsKey(channel.getId())){
            Channel existing = data.get(channel.getId());
            existing.updateName(channel.getName());
            existing.updatePrivate(channel.getIsPrivate());
            existing.updateUpdatedAt(System.currentTimeMillis());
            data.put(existing.getId(), existing);
        }
        else{
            data.put(channel.getId(), channel);
        }
        writeToFile(data);
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
        save(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        Map<UUID, Channel> data = load();
        if(!data.containsKey(id)){
            throw new NoSuchElementException("조회 실패 : 해당 ID의 채널을 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return List.copyOf(load().values());
    }

    @Override
    public Channel update(Channel channel) {
        if (read(channel.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        save(channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        remove(id);
    }

    private void remove(UUID id) {
        Map<UUID, Channel> data = load();
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 채널 ID입니다.");
        }
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, Channel>());
    }

    private void writeToFile(Map<UUID, Channel> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
