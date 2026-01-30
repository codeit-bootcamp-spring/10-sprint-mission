package com.sprint.mission.discodeit.repository.file;

import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private static final String CHANNEL_FILE = "data/channel.ser";

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(loadData().get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(Channel channel) {
        Map<UUID, Channel> data = loadData();
        data.put(channel.getId(), channel);
        saveData(data);
    }

    @Override
    public void delete(UUID channelId) {
        Map<UUID, Channel> data = loadData();
        data.remove(channelId);
        saveData(data);
    }

    private Map<UUID, Channel> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CHANNEL_FILE))){
            return (Map<UUID,Channel>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Channel> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(CHANNEL_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
