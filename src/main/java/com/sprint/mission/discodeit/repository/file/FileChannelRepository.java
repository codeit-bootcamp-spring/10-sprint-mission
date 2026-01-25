package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static FileChannelRepository instance = null;
    public static FileChannelRepository getInstance(){
        if(instance == null){
            instance = new FileChannelRepository();
        }
        return instance;
    }
    private FileChannelRepository(){}

    public static final String FILE_PATH = "channels.dat";

    @Override
    public void fileSave(Set<Channel> channels) {
        File file = new File(FILE_PATH);

        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(channels);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<Channel> fileLoadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashSet<>(); // 파일이 없으면 빈 셋 반환
        }

        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<Channel>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel fileLoad(UUID id) {
        return fileLoadAll().stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Channel not found: id = " + id));
    }


    @Override
    public void fileDelete(UUID id) {
        Set<Channel> channels = fileLoadAll();
        channels.removeIf(channel -> channel.getId().equals(id));
        fileSave(channels);
    }
}
