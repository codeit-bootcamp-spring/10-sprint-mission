package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file", matchIfMissing = true)
public class FileChannelRepository implements ChannelRepository {
    // 필드
    private final Path STORE_FILE;
    private List<Channel> channelData;

    public FileChannelRepository(@Value("${discodeit.repository.path}") String directoryPath) {
        Path BASE_PATH = Path.of(directoryPath).resolve("channel");

        this.STORE_FILE = BASE_PATH.resolve("channel.ser");
        init(BASE_PATH);
        loadData();
    }

    private void init(Path BASE_PATH) {
        try{
            if(!Files.exists(BASE_PATH)) {
                Files.createDirectories(BASE_PATH);
            }
        } catch(Exception e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    private void loadData() {
        if(!Files.exists(STORE_FILE)) {
            channelData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_FILE.toFile()))){
            channelData = (List<Channel>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    private void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE.toFile()))){
            oos.writeObject(channelData);
        } catch (Exception e){
            throw new RuntimeException("Data save failed." + e.getMessage());
        }
    }

    @Override
    public Channel find(UUID channelID) {
        loadData();
        return channelData.stream()
                .filter(channel -> channel.getId().equals(channelID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));
    }

    @Override
    public List<Channel> findAll() {
        loadData();
        return channelData;
    }

    @Override
    public void deleteChannel(UUID channelID) {
        loadData();
        channelData.removeIf(ch -> ch.getId().equals(channelID));
        saveData();
    }

    @Override
    public Channel save(Channel channel){
        loadData();
        // 덮어 씌우는 구조
        for(int i=0; i<channelData.size(); i++){
            if(channelData.get(i).getId().equals(channel.getId())){
                channelData.set(i, channel);
                saveData();
                return channel;
            }
        }
        // 없으면 추가
        channelData.add(channel);
        saveData();
        return channel;
    }
}
