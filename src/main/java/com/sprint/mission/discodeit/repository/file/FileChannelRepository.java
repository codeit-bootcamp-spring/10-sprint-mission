package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    // 필드
    private final Path basePath = Path.of("data/channel");
    private final Path storeFile = basePath.resolve("channel.ser");
    private List<Channel> channelData;

    public FileChannelRepository() {
        init();
        loadData();
    }

    private void init() {
        try{
            if(!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
        } catch(Exception e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    private void loadData() {
        if(!Files.exists(storeFile)) {
            channelData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            channelData = (List<Channel>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    private void saveData() {
        init();

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))){
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
    public void deleteChannel(Channel channel) {
        loadData();
        channelData.removeIf(ch -> ch.getId().equals(channel.getId()));
        saveData();
    }

    @Override
    public Channel save(Channel channel){
        loadData();
        channelData.removeIf(ch -> ch.getId().equals(channel.getId()));
        channelData.add(channel);
        saveData();
        return channel;
    }
}
