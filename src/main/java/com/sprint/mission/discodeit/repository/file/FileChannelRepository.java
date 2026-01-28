package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH = "channels.dat";
    private Map<UUID, Channel> data;

    public FileChannelRepository() {
        this.data = loadFromFile();
    }
    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
        saveToFile();
    }
    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
    @Override
    public Optional<Channel> findById(UUID id){

        return Optional.ofNullable(data.get(id));
    }
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }





    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("Channel 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


}
