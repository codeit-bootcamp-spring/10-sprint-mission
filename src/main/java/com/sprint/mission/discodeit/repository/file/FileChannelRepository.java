package com.sprint.mission.discodeit.repository.file;

import ch.qos.logback.core.pattern.parser.OptionTokenizer;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = false
)
public class FileChannelRepository implements ChannelRepository {
    private final String filePath;
    public FileChannelRepository(@Value("${discodeit.repository.file-directory}") String directory){
        this.filePath = directory + "/channel.ser";
    }

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
        File file = new File(filePath);
        if(!file.exists()) return new HashMap<>();

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID,Channel>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Channel> data){
        File file = new File(filePath);
        if(file.getParentFile() != null && !file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }

        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(file)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
