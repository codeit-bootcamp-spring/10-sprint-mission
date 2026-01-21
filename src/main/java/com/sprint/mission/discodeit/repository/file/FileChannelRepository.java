package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "channels.dat";
    private final Map<UUID, Channel> cache;
    public FileChannelRepository(){
        this.cache = loadData();
    }
    private Map<UUID, Channel> loadData(){
        File file = new File(FILE_PATH);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, Channel>) ois.readObject();
        } catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(cache);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void save(Channel channel) {
        cache.put(channel.getId(), channel);
        saveData();
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}
